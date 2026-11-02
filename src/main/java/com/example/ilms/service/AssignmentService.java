package com.example.ilms.service;

import com.example.ilms.dto.assignment.*;
import com.example.ilms.dto.common.PageResponse;
import com.example.ilms.entity.*;
import com.example.ilms.exception.AppException;
import com.example.ilms.exception.ErrorCode;
import com.example.ilms.repository.*;
import com.example.ilms.specification.AssignmentSpecification;
import jakarta.validation.Validator;
import jakarta.validation.groups.Default;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AssignmentService {
    private static final Set<String> SORT_FIELDS = Set.of("id", "title", "maxScore", "dueDate", "createdAt", "updatedAt");
    private final AssignmentRepository assignmentRepository;
    private final AssessmentRepository assessmentRepository;
    private final ClassRepository classRepository;
    private final ClassMemberRepository classMemberRepository;
    private final CourseRepository courseRepository;
    private final Validator validator;

    public PageResponse<AssignmentSummaryResponseDTO> getAssignments(UUID classId, UUID lessonId, String search,
            int page, int size, String sort, User user) {
        requireUser(user);
        Pageable pageable = pageable(page, size, sort);
        ClassEntity cls = classRepository.findById(classId).filter(c -> c.getDeletedAt() == null)
                .orElseThrow(() -> new AppException(ErrorCode.CLASS_NOT_FOUND));
        requireAccess(cls, user, false);
        Page<Assignment> result = assignmentRepository.findAll(AssignmentSpecification.inClass(classId, lessonId, search), pageable);
        List<AssignmentSummaryResponseDTO> items = result.getContent().stream().map(assignment -> {
            context(assignment, user, false);
            return summary(assignment, new AssignmentSummaryResponseDTO());
        }).toList();
        return PageResponse.<AssignmentSummaryResponseDTO>builder().items(items).pageNumber(page).pageSize(size)
                .totalElements(result.getTotalElements()).totalPages(result.getTotalPages()).isLast(result.isLast()).build();
    }

    public AssignmentDetailResponseDTO getAssignment(UUID id, User user) {
        requireUser(user);
        Assignment assignment = assignmentRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ASSIGNMENT_NOT_FOUND));
        return detail(assignment, context(assignment, user, false));
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public AssignmentDetailResponseDTO createAssignment(UUID classId, AssignmentRequestDTO request, User user) {
        requireUser(user);
        validate(request, true);
        ClassEntity cls = classRepository.findById(classId).filter(c -> c.getDeletedAt() == null)
                .orElseThrow(() -> new AppException(ErrorCode.CLASS_NOT_FOUND));
        requireAccess(cls, user, true);
        Course course = courseRepository.findById(request.getCourseId()).filter(c -> c.getDeletedAt() == null)
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));
        requireWritable(cls, course);
        validateLesson(request.getLessonId(), course.getId());
        UUID id = UUID.randomUUID();
        LocalDateTime now = now();
        Assessment assessment = Assessment.builder().id(id).classEntity(cls).course(course).createdBy(user)
                .title(request.getTitle()).createdAt(now).updatedAt(now).build();
        Assignment assignment = Assignment.builder().id(id).lessonId(request.getLessonId()).title(request.getTitle())
                .description(request.getDescription()).maxScore(request.getMaxScore()).dueDate(request.getDueDate())
                .allowLateSubmission(request.getAllowLateSubmission()).fileAttachmentUrl(request.getFileAttachmentUrl())
                .createdAt(now).updatedAt(now).build();
        assessmentRepository.saveAndFlush(assessment);
        assignmentRepository.saveAndFlush(assignment);
        return detail(assignment, assessment);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public AssignmentDetailResponseDTO updateAssignment(UUID id, AssignmentRequestDTO request, User user) {
        requireUser(user);
        validate(request, false);
        Assignment assignment = assignmentRepository.findForUpdate(id)
                .orElseThrow(() -> new AppException(ErrorCode.ASSIGNMENT_NOT_FOUND));
        Assessment assessment = context(assignment, user, true);
        requireWritable(assessment.getClassEntity(), assessment.getCourse());
        if (request.has("lessonId")) validateLesson(request.getLessonId(), assessment.getCourse().getId());
        if (request.has("maxScore") && request.getMaxScore().compareTo(assignment.getMaxScore()) < 0) {
            BigDecimal highest = assignmentRepository.findHighestGrade(id);
            if (highest != null && highest.compareTo(request.getMaxScore()) > 0) conflict();
        }
        if (request.getProvidedFields().isEmpty()) return detail(assignment, assessment);
        if (request.has("title")) { assignment.setTitle(request.getTitle()); assessment.setTitle(request.getTitle()); }
        if (request.has("lessonId")) assignment.setLessonId(request.getLessonId());
        if (request.has("description")) assignment.setDescription(request.getDescription());
        if (request.has("maxScore")) assignment.setMaxScore(request.getMaxScore());
        if (request.has("dueDate")) assignment.setDueDate(request.getDueDate());
        if (request.has("allowLateSubmission")) assignment.setAllowLateSubmission(request.getAllowLateSubmission());
        if (request.has("fileAttachmentUrl")) assignment.setFileAttachmentUrl(request.getFileAttachmentUrl());
        LocalDateTime now = now();
        assignment.setUpdatedAt(now); assessment.setUpdatedAt(now);
        assessmentRepository.saveAndFlush(assessment);
        assignmentRepository.saveAndFlush(assignment);
        return detail(assignment, assessment);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void deleteAssignment(UUID id, User user) {
        requireUser(user);
        Assignment assignment = assignmentRepository.findForUpdate(id)
                .orElseThrow(() -> new AppException(ErrorCode.ASSIGNMENT_NOT_FOUND));
        Assessment assessment = context(assignment, user, true);
        requireWritable(assessment.getClassEntity(), assessment.getCourse());
        if (assignmentRepository.countSubmissions(id) != 0) conflict();
        assignmentRepository.delete(assignment);
        assignmentRepository.flush();
        assessmentRepository.delete(assessment);
        assessmentRepository.flush();
    }

    private Assessment context(Assignment assignment, User user, boolean write) {
        Assessment assessment = assessmentRepository.findById(assignment.getId()).orElseThrow(
                () -> new AppException(ErrorCode.ASSIGNMENT_CONFLICT));
        ClassEntity cls = assessment.getClassEntity();
        Course course = assessment.getCourse();
        if (cls == null || course == null) conflict();
        if (cls.getDeletedAt() != null) throw new AppException(ErrorCode.CLASS_NOT_FOUND);
        if (course.getDeletedAt() != null) throw new AppException(ErrorCode.COURSE_NOT_FOUND);
        requireAccess(cls, user, write);
        BigDecimal score = assignment.getMaxScore();
        if (assignmentRepository.countExamWithSameId(assignment.getId()) != 0
                || assignment.getTitle() == null || !assignment.getTitle().equals(assessment.getTitle())
                || assignment.getTitle().isBlank() || score == null || score.signum() <= 0
                || score.compareTo(new BigDecimal("100")) > 0 || score.stripTrailingZeros().scale() > 2
                || assignment.getAllowLateSubmission() == null) conflict();
        return assessment;
    }

    private void requireUser(User user) {
        if (user == null || user.getId() == null) throw new AppException(ErrorCode.UNAUTHORIZED);
    }

    private void requireAccess(ClassEntity cls, User user, boolean write) {
        if (user.getAuthorities().stream().anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()))
                || (cls.getOwner() != null && user.getId().equals(cls.getOwner().getId()))) return;
        ClassMember member = classMemberRepository.findByClassEntity_IdAndUser_Id(cls.getId(), user.getId()).orElse(null);
        if (member != null && "ACTIVE".equals(member.getStatus())
                && ("TEACHER".equals(member.getRole()) || (!write && "STUDENT".equals(member.getRole())))) return;
        throw new AppException(ErrorCode.CLASS_ACCESS_DENIED);
    }

    private void requireWritable(ClassEntity cls, Course course) {
        if (Boolean.TRUE.equals(cls.getIsArchived())
                || !assessmentRepository.hasActiveCourseLink(cls.getId(), course.getId())) conflict();
    }

    private void validateLesson(UUID lessonId, UUID courseId) {
        if (lessonId == null) return;
        if (assignmentRepository.countLesson(lessonId) == 0) throw new AppException(ErrorCode.LESSON_NOT_FOUND);
        if (assignmentRepository.countLessonInCourse(lessonId, courseId) == 0) invalid();
    }

    private void validate(AssignmentRequestDTO request, boolean create) {
        if (request == null || !validator.validate(request, create ? AssignmentRequestDTO.Create.class : Default.class).isEmpty()
                || (!create && request.has("courseId"))) invalid();
    }

    private Pageable pageable(int page, int size, String value) {
        if (page < 0 || size < 1 || size > 100 || (long) page * size > Integer.MAX_VALUE) invalid();
        String[] parts = (value == null ? "createdAt,desc" : value).split(",", -1);
        if (parts.length != 2 || !SORT_FIELDS.contains(parts[0]) || (!"asc".equals(parts[1]) && !"desc".equals(parts[1]))) invalid();
        Sort sort = Sort.by("asc".equals(parts[1]) ? Sort.Direction.ASC : Sort.Direction.DESC, parts[0]);
        if (!"id".equals(parts[0])) sort = sort.and(Sort.by("id").ascending());
        return PageRequest.of(page, size, sort);
    }

    private <T extends AssignmentSummaryResponseDTO> T summary(Assignment assignment, T dto) {
        dto.setId(assignment.getId()); dto.setLessonId(assignment.getLessonId()); dto.setTitle(assignment.getTitle());
        dto.setMaxScore(assignment.getMaxScore()); dto.setDueDate(assignment.getDueDate());
        dto.setAllowLateSubmission(assignment.getAllowLateSubmission()); dto.setCreatedAt(assignment.getCreatedAt());
        return dto;
    }

    private AssignmentDetailResponseDTO detail(Assignment assignment, Assessment assessment) {
        AssignmentDetailResponseDTO dto = summary(assignment, new AssignmentDetailResponseDTO());
        dto.setClassId(assessment.getClassEntity().getId()); dto.setCourseId(assessment.getCourse().getId());
        dto.setDescription(assignment.getDescription()); dto.setFileAttachmentUrl(assignment.getFileAttachmentUrl());
        dto.setUpdatedAt(assignment.getUpdatedAt());
        return dto;
    }

    private LocalDateTime now() { return LocalDateTime.now().truncatedTo(ChronoUnit.MICROS); }
    private void invalid() { throw new AppException(ErrorCode.ASSIGNMENT_INVALID_REQUEST); }
    private void conflict() { throw new AppException(ErrorCode.ASSIGNMENT_CONFLICT); }
}
