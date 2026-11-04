package com.example.ilms.service;

import com.example.ilms.dto.common.PageResponse;
import com.example.ilms.dto.submission.*;
import com.example.ilms.entity.*;
import com.example.ilms.dto.common.BatchOperationResponseDTO;
import com.example.ilms.exception.AppException;
import com.example.ilms.exception.ErrorCode;
import com.example.ilms.repository.*;
import com.example.ilms.specification.SubmissionSpecification;
import com.example.ilms.type.SubmissionStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.annotation.*;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@RequiredArgsConstructor
public class SubmissionService {
    private final AssignmentRepository assignmentRepository;
    private final AssessmentRepository assessmentRepository;
    private final SubmissionRepository submissionRepository;
    private final GradingRepository gradingRepository;
    private final ClassMemberRepository classMemberRepository;
    private final PlatformTransactionManager transactionManager;
    private Clock clock = Clock.systemDefaultZone();

    @Transactional(readOnly = true)
    public PageResponse<SubmissionSummaryResponseDTO> getSubmissions(UUID assignmentId, String status,
            String search, int page, int size, String sort, User user) {
        authenticated(user);
        if (page < 0 || size < 1 || size > 100 || (long) page * size > Integer.MAX_VALUE) {
            throw new AppException(ErrorCode.SUBMISSION_INVALID_REQUEST);
        }
        if (status != null && !Set.of("SUBMITTED", "GRADED", "LATE").contains(status)) {
            throw new AppException(ErrorCode.SUBMISSION_INVALID_REQUEST);
        }
        String[] order = (sort == null ? "submittedAt,desc" : sort).split(",", -1);
        if (order.length != 2 || !Set.of("id", "submittedAt", "status").contains(order[0])
                || !Set.of("asc", "desc").contains(order[1])) {
            throw new AppException(ErrorCode.SUBMISSION_INVALID_REQUEST);
        }
        Context context = context(assignmentId, false, false);
        requireTeacher(context, user);
        if (!submissionRepository.findDuplicateStudents(assignmentId).isEmpty()) {
            throw new AppException(ErrorCode.SUBMISSION_DATA_INCONSISTENT);
        }
        Page<Submission> result = submissionRepository.findAll(SubmissionSpecification.filter(
                assignmentId, context.assignment().getDueDate(), status, search, order[0], "asc".equals(order[1])),
                PageRequest.of(page, size));
        List<SubmissionSummaryResponseDTO> items = result.getContent().stream().map(submission -> {
            validateSubmission(submission);
            Grading grading = submission.getGrading();
            validateGrading(grading, context.assignment());
            return summary(submission, context.assignment(), grading);
        }).toList();
        return PageResponse.<SubmissionSummaryResponseDTO>builder().items(items).pageNumber(page).pageSize(size)
                .totalElements(result.getTotalElements()).totalPages(result.getTotalPages()).isLast(result.isLast()).build();
    }

    @Transactional(readOnly = true)
    public SubmissionDetailResponseDTO getMySubmission(UUID assignmentId, User user) {
        authenticated(user);
        Context context = context(assignmentId, false, false);
        requireStudent(context, user);
        Submission submission = current(assignmentId, user.getId(), false);
        return detail(submission, context.assignment(), readGrading(submission));
    }

    @Transactional(readOnly = true)
    public SubmissionDetailResponseDTO getSubmission(UUID id, User user) {
        authenticated(user);
        Submission submission = submissionRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.SUBMISSION_NOT_FOUND));
        Context context = context(assessmentId(submission), false, true);
        if (!isTeacher(context, user)) {
            requireStudentOwner(context, submission, user);
        }
        validateCurrent(submission);
        return detail(submission, context.assignment(), readGrading(submission));
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public SubmissionDetailResponseDTO createSubmission(UUID assignmentId, SubmissionRequestDTO request, User user) {
        authenticated(user);
        validateContent(request);
        Context context = context(assignmentId, true, false);
        requireStudent(context, user);
        requireStudentWrites(context);
        List<Submission> existing = submissionRepository.findCurrentForUpdate(assignmentId, user.getId());
        if (!existing.isEmpty()) {
            throw new AppException(existing.size() > 1 ? ErrorCode.SUBMISSION_DATA_INCONSISTENT : ErrorCode.SUBMISSION_ALREADY_EXISTS);
        }
        LocalDateTime now = now();
        if (isAfterDeadline(context.assignment(), now) && !context.assignment().getAllowLateSubmission()) {
            throw new AppException(ErrorCode.SUBMISSION_DEADLINE_PASSED);
        }
        Submission submission = Submission.builder().assessment(context.assessment()).user(user)
                .attemptNumber(1).maxAttempts(1).submissionText(request.getContent()).submittedAt(now).build();
        submission.setStatus(status(submission, context.assignment(), null).name());
        submissionRepository.saveAndFlush(submission);
        return detail(submission, context.assignment(), null);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public SubmissionDetailResponseDTO updateSubmission(UUID assignmentId, SubmissionRequestDTO request, User user) {
        authenticated(user);
        validateContent(request);
        Context context = context(assignmentId, true, false);
        requireStudent(context, user);
        requireStudentWrites(context);
        Submission submission = current(assignmentId, user.getId(), true);
        Grading grading = gradingRepository.findForUpdate(submission.getId()).orElse(null);
        if (grading != null) throw new AppException(ErrorCode.SUBMISSION_ALREADY_GRADED);
        if (submission.getAttemptNumber() >= effectiveLimit(submission)) throw new AppException(ErrorCode.SUBMISSION_ATTEMPTS_EXHAUSTED);
        LocalDateTime now = now();
        if (isAfterDeadline(context.assignment(), now)) throw new AppException(ErrorCode.SUBMISSION_DEADLINE_PASSED);
        submission.setSubmissionText(request.getContent());
        submission.setAttemptNumber(submission.getAttemptNumber() + 1);
        submission.setSubmittedAt(now);
        submission.setStatus(status(submission, context.assignment(), null).name());
        submissionRepository.saveAndFlush(submission);
        return detail(submission, context.assignment(), null);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public SubmissionDetailResponseDTO gradeSubmission(UUID id, SubmissionGradeRequestDTO request, User user) {
        authenticated(user);
        validateGrade(request);
        UUID assignmentId = locateAssignment(id);
        Context context = context(assignmentId, true, true);
        requireTeacher(context, user);
        return gradeItem(context, id, request.getScore(), request.getFeedback(), request.isFeedbackPresent(), user);
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public BatchOperationResponseDTO batchGradeSubmissions(UUID assignmentId,
            BatchGradeSubmissionsRequestDTO request, User user) {
        authenticated(user);
        // Validate the entire payload before the first transaction can write a grade.
        if (request == null || request.getGrades() == null || request.getGrades().isEmpty()) {
            throw new AppException(ErrorCode.SUBMISSION_INVALID_REQUEST);
        }
        for (BatchGradeSubmissionsRequestDTO.GradeItem item : request.getGrades()) {
            if (item == null || item.getSubmissionId() == null) throw new AppException(ErrorCode.SUBMISSION_INVALID_REQUEST);
            validateScore(item.getScore());
            validateFeedback(item.getFeedback());
        }
        TransactionTemplate preflight = transaction(true);
        preflight.executeWithoutResult(ignored -> requireTeacher(context(assignmentId, false, false), user));
        List<UUID> successes = new ArrayList<>();
        List<BatchOperationResponseDTO.FailedItem> failures = new ArrayList<>();
        for (BatchGradeSubmissionsRequestDTO.GradeItem item : request.getGrades()) {
            try {
                transaction(false).executeWithoutResult(ignored -> {
                    Context context = context(assignmentId, true, false);
                    requireTeacher(context, user);
                    gradeItem(context, item.getSubmissionId(), item.getScore(), item.getFeedback(),
                            item.isFeedbackPresent(), user);
                });
                // Added only after execute returns and the item transaction has committed.
                successes.add(item.getSubmissionId());
            } catch (AppException ex) {
                failures.add(new BatchOperationResponseDTO.FailedItem(item.getSubmissionId(), ex.getErrorCode().getMessage()));
            } catch (DataAccessException | org.springframework.transaction.TransactionException ex) {
                failures.add(new BatchOperationResponseDTO.FailedItem(item.getSubmissionId(),
                        ErrorCode.SUBMISSION_WRITE_CONFLICT.getMessage()));
            }
        }
        return BatchOperationResponseDTO.builder().totalRequested(request.getGrades().size())
                .successCount(successes.size()).failedCount(failures.size())
                .successIds(successes).failedItems(failures).build();
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void deleteSubmission(UUID id, User user) {
        authenticated(user);
        Context context = context(locateAssignment(id), true, true);
        Submission submission = submissionRepository.findForUpdate(id).orElseThrow(() -> new AppException(ErrorCode.SUBMISSION_NOT_FOUND));
        if (!isAdmin(user)) requireStudentOwner(context, submission, user);
        requireSameAssignment(submission, context.assignment().getId());
        requireStudentWrites(context);
        validateCurrent(submission);
        if (gradingRepository.findForUpdate(id).isPresent()) throw new AppException(ErrorCode.SUBMISSION_ALREADY_GRADED);
        submissionRepository.delete(submission);
        submissionRepository.flush();
    }

    private SubmissionDetailResponseDTO gradeItem(Context context, UUID submissionId, BigDecimal score,
            String feedback, boolean feedbackPresent, User user) {
        // Reject a batch item from another Assignment before taking its Submission lock.
        // Every writer locks its own Assignment first, then that Assignment's Submission/Grading.
        if (!context.assignment().getId().equals(locateAssignment(submissionId))) {
            throw new AppException(ErrorCode.SUBMISSION_INVALID_REQUEST);
        }
        Submission submission = submissionRepository.findForUpdate(submissionId)
                .orElseThrow(() -> new AppException(ErrorCode.SUBMISSION_NOT_FOUND));
        requireSameAssignment(submission, context.assignment().getId());
        validateCurrent(submission);
        if (score.compareTo(context.assignment().getMaxScore()) > 0) throw new AppException(ErrorCode.SUBMISSION_INVALID_REQUEST);
        Grading grading = gradingRepository.findForUpdate(submissionId).orElse(null);
        validateGrading(grading, context.assignment());
        if (grading != null && !isAdmin(user)
                && (grading.getGrader() == null || !user.getId().equals(grading.getGrader().getId()))) {
            throw new AppException(ErrorCode.CLASS_ACCESS_DENIED);
        }
        LocalDateTime now = now();
        if (grading == null) {
            grading = Grading.builder().submission(submission).createdAt(now).build();
        }
        grading.setScore(score);
        if (feedbackPresent) grading.setFeedback(feedback);
        grading.setGrader(user);
        grading.setGradedAt(now);
        grading.setUpdatedAt(now);
        // correctAnswersCount/scaledScore are intentionally preserved on an existing result.
        gradingRepository.saveAndFlush(grading);
        submission.setStatus(SubmissionStatus.GRADED.name());
        submissionRepository.saveAndFlush(submission);
        return detail(submission, context.assignment(), grading);
    }

    private TransactionTemplate transaction(boolean readOnly) {
        TransactionTemplate template = new TransactionTemplate(transactionManager);
        template.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        template.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
        template.setReadOnly(readOnly);
        return template;
    }

    private Context context(UUID id, boolean lock, boolean referencedBySubmission) {
        Assignment assignment = (lock ? assignmentRepository.findForUpdate(id) : assignmentRepository.findById(id))
                .orElseThrow(() -> referencedBySubmission ? new AppException(ErrorCode.SUBMISSION_DATA_INCONSISTENT)
                        : new AppException(ErrorCode.ASSIGNMENT_NOT_FOUND));
        Assessment assessment = assessmentRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.SUBMISSION_DATA_INCONSISTENT));
        if (assessment.getClassEntity() == null || assessment.getCourse() == null) {
            throw new AppException(ErrorCode.SUBMISSION_DATA_INCONSISTENT);
        }
        if (assessment.getClassEntity().getDeletedAt() != null) throw new AppException(ErrorCode.CLASS_NOT_FOUND);
        if (assessment.getCourse().getDeletedAt() != null) throw new AppException(ErrorCode.COURSE_NOT_FOUND);
        if (assignmentRepository.countExamWithSameId(id) != 0) throw new AppException(ErrorCode.SUBMISSION_DATA_INCONSISTENT);
        if (!validScore(assignment.getMaxScore()) || assignment.getMaxScore().signum() <= 0
                || assignment.getAllowLateSubmission() == null) {
            throw new AppException(ErrorCode.SUBMISSION_DATA_INCONSISTENT);
        }
        if (assignment.getTitle() == null || assignment.getTitle().isBlank()
                || !assignment.getTitle().equals(assessment.getTitle())) {
            throw new AppException(ErrorCode.SUBMISSION_DATA_INCONSISTENT);
        }
        return new Context(assignment, assessment);
    }

    private UUID locateAssignment(UUID submissionId) {
        Optional<UUID> id = submissionRepository.findAssessmentId(submissionId);
        if (id.isPresent()) return id.get();
        if (submissionRepository.existsById(submissionId)) throw new AppException(ErrorCode.SUBMISSION_DATA_INCONSISTENT);
        throw new AppException(ErrorCode.SUBMISSION_NOT_FOUND);
    }

    private Submission current(UUID assignmentId, UUID userId, boolean lock) {
        List<Submission> rows = lock ? submissionRepository.findCurrentForUpdate(assignmentId, userId)
                : submissionRepository.findByAssessment_IdAndUser_Id(assignmentId, userId);
        if (rows.isEmpty()) throw new AppException(ErrorCode.SUBMISSION_NOT_FOUND);
        if (rows.size() > 1) throw new AppException(ErrorCode.SUBMISSION_DATA_INCONSISTENT);
        validateSubmission(rows.getFirst());
        return rows.getFirst();
    }

    private void validateCurrent(Submission submission) {
        validateSubmission(submission);
        if (submissionRepository.countByAssessment_IdAndUser_Id(assessmentId(submission), submission.getUser().getId()) != 1) {
            throw new AppException(ErrorCode.SUBMISSION_DATA_INCONSISTENT);
        }
    }

    private void validateSubmission(Submission submission) {
        assessmentId(submission);
        if (submission.getUser() == null || submission.getUser().getId() == null || submission.getSubmittedAt() == null
                || submission.getAttemptNumber() == null || submission.getAttemptNumber() < 1
                || effectiveLimit(submission) < 1 || submission.getAttemptNumber() > effectiveLimit(submission)
                || submission.getSubmissionText() == null || submission.getSubmissionText().isBlank()) {
            throw new AppException(ErrorCode.SUBMISSION_DATA_INCONSISTENT);
        }
    }

    private UUID assessmentId(Submission submission) {
        if (submission.getAssessment() == null || submission.getAssessment().getId() == null) {
            throw new AppException(ErrorCode.SUBMISSION_DATA_INCONSISTENT);
        }
        return submission.getAssessment().getId();
    }

    private void requireSameAssignment(Submission submission, UUID assignmentId) {
        if (!assignmentId.equals(assessmentId(submission))) throw new AppException(ErrorCode.SUBMISSION_INVALID_REQUEST);
    }

    private Grading readGrading(Submission submission) {
        return gradingRepository.findBySubmission_Id(submission.getId()).orElse(null);
    }

    private int effectiveLimit(Submission submission) {
        return submission.getMaxAttempts() == null ? 1 : submission.getMaxAttempts();
    }

    private boolean isAdmin(User user) {
        return user.getAuthorities().stream().anyMatch(authority -> "ROLE_ADMIN".equals(authority.getAuthority()));
    }

    private boolean isTeacher(Context context, User user) {
        if (isAdmin(user)) return true;
        ClassEntity cls = context.assessment().getClassEntity();
        if (cls.getOwner() != null && user.getId().equals(cls.getOwner().getId())) return true;
        return hasClassRole(cls.getId(), user.getId(), "TEACHER");
    }

    private boolean hasClassRole(UUID classId, UUID userId, String role) {
        return classMemberRepository.findByClassEntity_IdAndUser_Id(classId, userId)
                .filter(member -> "ACTIVE".equals(member.getStatus()) && role.equals(member.getRole())).isPresent();
    }

    private void requireTeacher(Context context, User user) {
        if (!isTeacher(context, user)) throw new AppException(ErrorCode.CLASS_ACCESS_DENIED);
    }

    private void requireStudent(Context context, User user) {
        if (!hasClassRole(context.assessment().getClassEntity().getId(), user.getId(), "STUDENT")) {
            throw new AppException(ErrorCode.CLASS_ACCESS_DENIED);
        }
    }

    private void requireStudentOwner(Context context, Submission submission, User user) {
        requireStudent(context, user);
        if (submission.getUser() == null || !user.getId().equals(submission.getUser().getId())) {
            throw new AppException(ErrorCode.CLASS_ACCESS_DENIED);
        }
    }

    private void requireStudentWrites(Context context) {
        if (Boolean.TRUE.equals(context.assessment().getClassEntity().getIsArchived())
                || !assessmentRepository.hasActiveCourseLink(context.assessment().getClassEntity().getId(),
                context.assessment().getCourse().getId())) {
            throw new AppException(ErrorCode.SUBMISSION_CLASS_READ_ONLY);
        }
    }

    private void authenticated(User user) {
        if (user == null || user.getId() == null) throw new AppException(ErrorCode.UNAUTHORIZED);
    }

    private void validateContent(SubmissionRequestDTO request) {
        if (request == null || request.getContent() == null || request.getContent().isBlank()) {
            throw new AppException(ErrorCode.SUBMISSION_INVALID_REQUEST);
        }
        if (request.getAttachmentIds() != null && !request.getAttachmentIds().isEmpty()) {
            throw new AppException(ErrorCode.SUBMISSION_INVALID_REQUEST);
        }
    }

    private void validateGrade(SubmissionGradeRequestDTO request) {
        if (request == null) throw new AppException(ErrorCode.SUBMISSION_INVALID_REQUEST);
        validateScore(request.getScore());
        validateFeedback(request.getFeedback());
        if (request.getRubricScores() != null && !request.getRubricScores().isEmpty()) {
            throw new AppException(ErrorCode.SUBMISSION_INVALID_REQUEST);
        }
    }

    private boolean validScore(BigDecimal score) {
        return score != null && score.signum() >= 0 && score.compareTo(new BigDecimal("100")) <= 0
                && score.stripTrailingZeros().scale() <= 2;
    }

    private void validateScore(BigDecimal score) {
        if (!validScore(score)) throw new AppException(ErrorCode.SUBMISSION_INVALID_REQUEST);
    }

    private void validateFeedback(String feedback) {
        if (feedback != null && feedback.getBytes(StandardCharsets.UTF_8).length > 65535) {
            throw new AppException(ErrorCode.SUBMISSION_INVALID_REQUEST);
        }
    }

    private void validateGrading(Grading grading, Assignment assignment) {
        if (grading != null && (!validScore(grading.getScore()) || grading.getScore().compareTo(assignment.getMaxScore()) > 0
                || grading.getGrader() == null || grading.getGrader().getId() == null || grading.getGradedAt() == null
                || (grading.getCorrectAnswersCount() != null && grading.getCorrectAnswersCount() < 0)
                || (grading.getScaledScore() != null && (grading.getScaledScore().signum() < 0
                || grading.getScaledScore().compareTo(new BigDecimal("999.99")) > 0
                || grading.getScaledScore().stripTrailingZeros().scale() > 2)))) {
            throw new AppException(ErrorCode.SUBMISSION_DATA_INCONSISTENT);
        }
    }

    private LocalDateTime now() {
        return LocalDateTime.now(clock).truncatedTo(ChronoUnit.MICROS);
    }

    private boolean isAfterDeadline(Assignment assignment, LocalDateTime time) {
        return assignment.getDueDate() != null && time.isAfter(assignment.getDueDate());
    }

    private SubmissionStatus status(Submission submission, Assignment assignment, Grading grading) {
        if (grading != null) return SubmissionStatus.GRADED;
        if (isAfterDeadline(assignment, submission.getSubmittedAt())) return SubmissionStatus.LATE;
        return submission.getAttemptNumber() > 1 ? SubmissionStatus.RESUBMITTED : SubmissionStatus.SUBMITTED;
    }

    private SubmissionSummaryResponseDTO summary(Submission submission, Assignment assignment, Grading grading) {
        return SubmissionSummaryResponseDTO.builder().id(submission.getId()).studentId(submission.getUser().getId())
                .studentCode(submission.getUser().getId().toString()).studentName(submission.getUser().getFullName())
                .studentAvatar(submission.getUser().getAvatarUrl()).status(status(submission, assignment, grading).name())
                .submittedAt(submission.getSubmittedAt()).isLate(isAfterDeadline(assignment, submission.getSubmittedAt()))
                .score(grading == null ? null : grading.getScore()).attachmentCount(0).build();
    }

    private SubmissionDetailResponseDTO detail(Submission submission, Assignment assignment, Grading grading) {
        validateSubmission(submission);
        validateGrading(grading, assignment);
        return SubmissionDetailResponseDTO.builder().id(submission.getId()).assignmentId(assignment.getId())
                .assignmentTitle(assignment.getTitle()).studentId(submission.getUser().getId())
                .studentName(submission.getUser().getFullName()).content(submission.getSubmissionText()).attachments(List.of())
                .status(status(submission, assignment, grading).name()).submittedAt(submission.getSubmittedAt())
                .isLate(isAfterDeadline(assignment, submission.getSubmittedAt())).score(grading == null ? null : grading.getScore())
                .feedback(grading == null ? null : grading.getFeedback())
                .gradedByTeacherId(grading == null ? null : grading.getGrader().getId())
                .gradedByTeacherName(grading == null ? null : grading.getGrader().getFullName())
                .gradedAt(grading == null ? null : grading.getGradedAt()).submissionAttempt(submission.getAttemptNumber()).build();
    }

    private record Context(Assignment assignment, Assessment assessment) { }
}
