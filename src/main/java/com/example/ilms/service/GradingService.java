package com.example.ilms.service;

import com.example.ilms.dto.common.BatchOperationResponseDTO;
import com.example.ilms.dto.grading.*;
import com.example.ilms.entity.*;
import com.example.ilms.exception.AppException;
import com.example.ilms.exception.ErrorCode;
import com.example.ilms.repository.*;
import com.example.ilms.type.SubmissionStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.annotation.*;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GradingService {
    private final GradingRepository gradingRepository;
    private final SubmissionRepository submissionRepository;
    private final AssignmentRepository assignmentRepository;
    private final AssessmentRepository assessmentRepository;
    private final GradingAssessmentRepository gradingAssessmentRepository;
    private final ClassMemberRepository classMemberRepository;
    private final PlatformTransactionManager transactionManager;
    private Clock clock = Clock.systemDefaultZone();

    @Transactional(readOnly = true)
    public GradingResponseDTO getSubmissionGrading(UUID submissionId, User user) {
        authenticated(user);
        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new AppException(ErrorCode.SUBMISSION_NOT_FOUND));
        Context context = context(assessmentId(submission), false);
        requireReader(context, submission, user);
        validateSubmission(submission, context);
        Grading grading = gradingRepository.findBySubmission_Id(submissionId)
                .orElseThrow(() -> new AppException(ErrorCode.GRADING_NOT_FOUND));
        validateGrading(grading, submissionId, context);
        return response(grading);
    }

    @Transactional(readOnly = true)
    public GradingResponseDTO getGrading(UUID id, User user) {
        authenticated(user);
        Grading grading = gradingRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.GRADING_NOT_FOUND));
        Submission submission = grading.getSubmission();
        if (submission == null || submission.getId() == null) inconsistent();
        Context context = context(assessmentId(submission), false);
        requireReader(context, submission, user);
        validateSubmission(submission, context);
        validateGrading(grading, submission.getId(), context);
        return response(grading);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public GradingResponseDTO createGrading(UUID submissionId, GradingRequestDTO request, User user) {
        authenticated(user);
        validateRequest(request, true);
        return createItem(submissionId, request, user);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public GradingResponseDTO updateGrading(UUID id, GradingRequestDTO request, User user) {
        authenticated(user);
        validateRequest(request, false);
        UUID submissionId = locateSubmission(id);
        Context context = context(locateAssessment(submissionId), true);
        requireTeacher(context, user);
        Submission submission = lockedSubmission(submissionId, context);
        Grading grading = lockedGrading(id, submissionId);
        validateGrading(grading, submissionId, context);
        requireGrader(grading, user);
        if (request.getProvidedFields().isEmpty()) return response(grading);
        if (request.has("score") && request.getScore().compareTo(context.maxScore()) > 0) invalid();
        if (request.has("score")) grading.setScore(request.getScore());
        if (request.has("feedback")) grading.setFeedback(request.getFeedback());
        if (request.has("correctAnswersCount")) grading.setCorrectAnswersCount(request.getCorrectAnswersCount());
        if (request.has("scaledScore")) grading.setScaledScore(request.getScaledScore());
        LocalDateTime now = now();
        grading.setGrader(user);
        grading.setGradedAt(now);
        grading.setUpdatedAt(now);
        gradingRepository.saveAndFlush(grading);
        submission.setStatus(SubmissionStatus.GRADED.name());
        submission.setGrading(grading);
        submissionRepository.saveAndFlush(submission);
        return response(grading);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void deleteGrading(UUID id, User user) {
        authenticated(user);
        UUID submissionId = locateSubmission(id);
        Context context = context(locateAssessment(submissionId), true);
        requireTeacher(context, user);
        Submission submission = lockedSubmission(submissionId, context);
        Grading grading = lockedGrading(id, submissionId);
        validateGrading(grading, submissionId, context);
        requireGrader(grading, user);
        submission.setGrading(null);
        gradingRepository.delete(grading);
        gradingRepository.flush();
        submission.setStatus(ungradedStatus(submission, context).name());
        submissionRepository.saveAndFlush(submission);
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public BatchOperationResponseDTO batchCreateGradings(BatchCreateGradingsRequestDTO request, User user) {
        authenticated(user);
        if (request == null || request.getItems() == null || request.getItems().isEmpty()) invalid();
        // Structural/value errors reject the whole payload before any item transaction can write.
        for (BatchCreateGradingsRequestDTO.Item item : request.getItems()) {
            if (item == null || item.getSubmissionId() == null) invalid();
            validateRequest(item, true);
        }
        transaction(true).executeWithoutResult(ignored -> {
            if (!isAdmin(user) && !hasAuthority(user, "ROLE_TEACHER")
                    && !gradingAssessmentRepository.canManageAnyClass(user.getId())) {
                throw new AppException(ErrorCode.CLASS_ACCESS_DENIED);
            }
        });
        List<UUID> successes = new ArrayList<>();
        List<BatchOperationResponseDTO.FailedItem> failures = new ArrayList<>();
        for (BatchCreateGradingsRequestDTO.Item item : request.getItems()) {
            try {
                UUID gradingId = transaction(false).execute(ignored ->
                        createItem(item.getSubmissionId(), item, user).getId());
                // This is a Grading ID, recorded only after the item transaction has committed.
                successes.add(gradingId);
            } catch (AppException ex) {
                failures.add(new BatchOperationResponseDTO.FailedItem(item.getSubmissionId(), ex.getErrorCode().getMessage()));
            } catch (DataAccessException | TransactionException ex) {
                failures.add(new BatchOperationResponseDTO.FailedItem(item.getSubmissionId(),
                        ErrorCode.GRADING_WRITE_CONFLICT.getMessage()));
            }
        }
        return BatchOperationResponseDTO.builder().totalRequested(request.getItems().size())
                .successCount(successes.size()).failedCount(failures.size())
                .successIds(successes).failedItems(failures).build();
    }

    private GradingResponseDTO createItem(UUID submissionId, GradingRequestDTO request, User user) {
        // Match SubmissionService: lock the Assignment first, then its Submission and Grading.
        // Exam-backed results use the existing Exam row as the corresponding lock resource.
        Context context = context(locateAssessment(submissionId), true);
        requireTeacher(context, user);
        Submission submission = lockedSubmission(submissionId, context);
        if (gradingRepository.findForUpdate(submissionId).isPresent()) {
            throw new AppException(ErrorCode.GRADING_ALREADY_EXISTS);
        }
        if (request.getScore().compareTo(context.maxScore()) > 0) invalid();
        LocalDateTime now = now();
        Grading grading = Grading.builder().submission(submission).grader(user).score(request.getScore())
                .feedback(request.getFeedback()).correctAnswersCount(request.getCorrectAnswersCount())
                .scaledScore(request.getScaledScore()).gradedAt(now).createdAt(now).updatedAt(now).build();
        gradingRepository.saveAndFlush(grading);
        submission.setStatus(SubmissionStatus.GRADED.name());
        submission.setGrading(grading);
        submissionRepository.saveAndFlush(submission);
        return response(grading);
    }

    private Context context(UUID id, boolean lock) {
        Assignment assignment = (lock ? assignmentRepository.findForUpdate(id) : assignmentRepository.findById(id)).orElse(null);
        GradingAssessmentRepository.ExamContext exam = (lock
                ? gradingAssessmentRepository.findExamContextForUpdate(id)
                : gradingAssessmentRepository.findExamContext(id)).orElse(null);
        // Exactly one subtype must exist for the Assessment referenced by a Submission.
        if ((assignment == null) == (exam == null)) inconsistent();
        Assessment assessment = assessmentRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.GRADING_DATA_INCONSISTENT));
        if (assessment.getClassEntity() == null || assessment.getCourse() == null
                || assessment.getTitle() == null || assessment.getTitle().isBlank()) inconsistent();
        if (assessment.getClassEntity().getDeletedAt() != null) throw new AppException(ErrorCode.CLASS_NOT_FOUND);
        if (assessment.getCourse().getDeletedAt() != null) throw new AppException(ErrorCode.COURSE_NOT_FOUND);
        BigDecimal maxScore;
        LocalDateTime deadline;
        if (assignment != null) {
            if (!assessment.getTitle().equals(assignment.getTitle()) || assignment.getAllowLateSubmission() == null) inconsistent();
            maxScore = assignment.getMaxScore();
            deadline = assignment.getDueDate();
        } else {
            maxScore = exam.getMaxScore();
            deadline = exam.getDeadline();
            if (deadline == null) inconsistent();
        }
        if (!validDecimal(maxScore, new BigDecimal("100")) || maxScore.signum() <= 0) inconsistent();
        return new Context(assessment, maxScore, deadline, assignment != null);
    }

    private Submission lockedSubmission(UUID id, Context context) {
        Submission submission = submissionRepository.findForUpdate(id)
                .orElseThrow(() -> new AppException(ErrorCode.SUBMISSION_NOT_FOUND));
        if (!context.assessment().getId().equals(assessmentId(submission))) {
            throw new AppException(ErrorCode.GRADING_WRITE_CONFLICT);
        }
        validateSubmission(submission, context);
        return submission;
    }

    private Grading lockedGrading(UUID id, UUID submissionId) {
        return gradingRepository.findForUpdate(submissionId).filter(grading -> id.equals(grading.getId()))
                .orElseThrow(() -> new AppException(ErrorCode.GRADING_NOT_FOUND));
    }

    private UUID locateSubmission(UUID id) {
        return gradingAssessmentRepository.findSubmissionId(id).orElseThrow(() ->
                new AppException(gradingRepository.existsById(id)
                        ? ErrorCode.GRADING_DATA_INCONSISTENT : ErrorCode.GRADING_NOT_FOUND));
    }

    private UUID locateAssessment(UUID submissionId) {
        return submissionRepository.findAssessmentId(submissionId).orElseThrow(() ->
                new AppException(submissionRepository.existsById(submissionId)
                        ? ErrorCode.GRADING_DATA_INCONSISTENT : ErrorCode.SUBMISSION_NOT_FOUND));
    }

    private UUID assessmentId(Submission submission) {
        if (submission.getAssessment() == null || submission.getAssessment().getId() == null) inconsistent();
        return submission.getAssessment().getId();
    }

    private void validateSubmission(Submission submission, Context context) {
        assessmentId(submission);
        int limit = submission.getMaxAttempts() == null ? 1 : submission.getMaxAttempts();
        if (submission.getUser() == null || submission.getUser().getId() == null || submission.getSubmittedAt() == null
                || submission.getAttemptNumber() == null || submission.getAttemptNumber() < 1 || limit < 1
                || submission.getAttemptNumber() > limit) inconsistent();
        if (context.assignment()) {
            if (submission.getSubmissionText() == null || submission.getSubmissionText().isBlank()
                    || submissionRepository.countByAssessment_IdAndUser_Id(
                    context.assessment().getId(), submission.getUser().getId()) != 1) inconsistent();
        }
    }

    private void validateGrading(Grading grading, UUID submissionId, Context context) {
        if (grading.getSubmission() == null || !submissionId.equals(grading.getSubmission().getId())
                || !validDecimal(grading.getScore(), context.maxScore())
                || grading.getGrader() == null || grading.getGrader().getId() == null || grading.getGradedAt() == null
                || (grading.getCorrectAnswersCount() != null && grading.getCorrectAnswersCount() < 0)
                || (grading.getScaledScore() != null && !validDecimal(grading.getScaledScore(), new BigDecimal("999.99")))) {
            inconsistent();
        }
    }

    private void requireReader(Context context, Submission submission, User user) {
        if (isTeacher(context, user)) return;
        if (submission.getUser() == null || !user.getId().equals(submission.getUser().getId())
                || !hasClassRole(context.assessment().getClassEntity().getId(), user.getId(), "STUDENT")) {
            throw new AppException(ErrorCode.CLASS_ACCESS_DENIED);
        }
    }

    private void requireTeacher(Context context, User user) {
        if (!isTeacher(context, user)) throw new AppException(ErrorCode.CLASS_ACCESS_DENIED);
    }

    private void requireGrader(Grading grading, User user) {
        if (!isAdmin(user) && !user.getId().equals(grading.getGrader().getId())) {
            throw new AppException(ErrorCode.CLASS_ACCESS_DENIED);
        }
    }

    private boolean isTeacher(Context context, User user) {
        if (isAdmin(user)) return true;
        ClassEntity cls = context.assessment().getClassEntity();
        return (cls.getOwner() != null && user.getId().equals(cls.getOwner().getId()))
                || hasClassRole(cls.getId(), user.getId(), "TEACHER");
    }

    private boolean hasClassRole(UUID classId, UUID userId, String role) {
        return classMemberRepository.findByClassEntity_IdAndUser_Id(classId, userId)
                .filter(member -> "ACTIVE".equals(member.getStatus()) && role.equals(member.getRole())).isPresent();
    }

    private boolean isAdmin(User user) { return hasAuthority(user, "ROLE_ADMIN"); }

    private boolean hasAuthority(User user, String authority) {
        return user.getAuthorities().stream().anyMatch(value -> authority.equals(value.getAuthority()));
    }

    private void authenticated(User user) {
        if (user == null || user.getId() == null) throw new AppException(ErrorCode.UNAUTHORIZED);
    }

    private void validateRequest(GradingRequestDTO request, boolean create) {
        if (request == null) invalid();
        if ((create || request.has("score")) && !validDecimal(request.getScore(), new BigDecimal("100"))) invalid();
        if (request.getCorrectAnswersCount() != null && request.getCorrectAnswersCount() < 0) invalid();
        if (request.getScaledScore() != null && !validDecimal(request.getScaledScore(), new BigDecimal("999.99"))) invalid();
        if (request.getFeedback() != null && request.getFeedback().getBytes(StandardCharsets.UTF_8).length > 65535) invalid();
    }

    private boolean validDecimal(BigDecimal score, BigDecimal maximum) {
        return score != null && score.signum() >= 0 && score.compareTo(maximum) <= 0
                && score.stripTrailingZeros().scale() <= 2;
    }

    private SubmissionStatus ungradedStatus(Submission submission, Context context) {
        if (context.deadline() != null && submission.getSubmittedAt().isAfter(context.deadline())) return SubmissionStatus.LATE;
        return submission.getAttemptNumber() > 1 ? SubmissionStatus.RESUBMITTED : SubmissionStatus.SUBMITTED;
    }

    private LocalDateTime now() { return LocalDateTime.now(clock).truncatedTo(ChronoUnit.MICROS); }

    private TransactionTemplate transaction(boolean readOnly) {
        TransactionTemplate template = new TransactionTemplate(transactionManager);
        template.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        template.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
        template.setReadOnly(readOnly);
        return template;
    }

    private GradingResponseDTO response(Grading grading) {
        return GradingResponseDTO.builder().id(grading.getId()).submissionId(grading.getSubmission().getId())
                .graderId(grading.getGrader().getId()).graderName(grading.getGrader().getFullName())
                .score(grading.getScore()).feedback(grading.getFeedback()).gradedAt(grading.getGradedAt())
                .correctAnswersCount(grading.getCorrectAnswersCount()).scaledScore(grading.getScaledScore())
                .createdAt(grading.getCreatedAt()).updatedAt(grading.getUpdatedAt()).build();
    }

    private void invalid() { throw new AppException(ErrorCode.GRADING_INVALID_REQUEST); }
    private void inconsistent() { throw new AppException(ErrorCode.GRADING_DATA_INCONSISTENT); }

    private record Context(Assessment assessment, BigDecimal maxScore, LocalDateTime deadline, boolean assignment) { }
}
