package com.example.ilms.controller;

import com.example.ilms.constant.SuccessCode;
import com.example.ilms.dto.assignment.*;
import com.example.ilms.dto.common.PageResponse;
import com.example.ilms.dto.response.ApiResponse;
import com.example.ilms.dto.response.ErrorResponse;
import com.example.ilms.dto.submission.SubmissionJsonDeserializer;
import com.example.ilms.entity.User;
import com.example.ilms.exception.AppException;
import com.example.ilms.exception.ErrorCode;
import com.example.ilms.service.AssignmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Slf4j
public class AssignmentController {
    private final AssignmentService assignmentService;

    @GetMapping("/classes/{classId}/assignments")
    public ResponseEntity<ApiResponse<PageResponse<AssignmentSummaryResponseDTO>>> getAssignments(
            @PathVariable String classId, @RequestParam(required = false) String lessonId,
            @RequestParam(required = false) String search, @RequestParam(required = false) String page,
            @RequestParam(required = false) String size, @RequestParam(required = false) String sort,
            @AuthenticationPrincipal User user) {
        return response(SuccessCode.GET_ASSIGNMENTS_SUCCESS, assignmentService.getAssignments(id(classId),
                lessonId == null ? null : id(lessonId), search, integer(page, 0), integer(size, 10), sort, user));
    }

    @GetMapping("/assignments/{id}")
    public ResponseEntity<ApiResponse<AssignmentDetailResponseDTO>> getAssignment(@PathVariable String id,
            @AuthenticationPrincipal User user) {
        return response(SuccessCode.GET_ASSIGNMENT_SUCCESS, assignmentService.getAssignment(id(id), user));
    }

    @PostMapping(value = "/classes/{classId}/assignments", consumes = "application/json")
    public ResponseEntity<ApiResponse<AssignmentDetailResponseDTO>> createAssignment(@PathVariable String classId,
            @RequestBody @Validated(AssignmentRequestDTO.Create.class) AssignmentRequestDTO request,
            @AuthenticationPrincipal User user) {
        return response(SuccessCode.CREATE_ASSIGNMENT_SUCCESS, assignmentService.createAssignment(id(classId), request, user));
    }

    @RequestMapping(value = "/assignments/{id}", method = {RequestMethod.PUT, RequestMethod.PATCH}, consumes = "application/json")
    public ResponseEntity<ApiResponse<AssignmentDetailResponseDTO>> updateAssignment(@PathVariable String id,
            @RequestBody @Valid AssignmentRequestDTO request, @AuthenticationPrincipal User user) {
        return response(SuccessCode.UPDATE_ASSIGNMENT_SUCCESS, assignmentService.updateAssignment(id(id), request, user));
    }

    @DeleteMapping("/assignments/{id}")
    public ResponseEntity<Void> deleteAssignment(@PathVariable String id, @AuthenticationPrincipal User user) {
        assignmentService.deleteAssignment(id(id), user);
        return ResponseEntity.noContent().build();
    }

    // Local to this controller: the shared error handler and ObjectMapper remain unchanged.
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> invalidJson(HttpMessageNotReadableException ex) {
        return error(ErrorCode.ASSIGNMENT_INVALID_REQUEST);
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ErrorResponse> persistenceFailure(DataAccessException ex) {
        log.error("Assignment persistence operation failed", ex);
        // A schema/connection/SQL failure is not a business conflict and must not be reported as a passed check.
        if (ex instanceof org.springframework.dao.ConcurrencyFailureException
                || ex instanceof org.springframework.dao.DataIntegrityViolationException) return error(ErrorCode.ASSIGNMENT_CONFLICT);
        return ResponseEntity.internalServerError().body(ErrorResponse.builder().status(500)
                .message("Không thể truy cập dữ liệu bài tập")
                .timestamp(LocalDateTime.now()).build());
    }

    private ResponseEntity<ErrorResponse> error(ErrorCode code) {
        return ResponseEntity.status(code.getStatus()).body(ErrorResponse.builder().status(code.getStatus().value())
                .message(code.getMessage()).timestamp(LocalDateTime.now()).build());
    }

    private UUID id(String value) {
        try { return SubmissionJsonDeserializer.parseId(value); }
        catch (IllegalArgumentException ex) { throw new AppException(ErrorCode.ASSIGNMENT_INVALID_REQUEST); }
    }

    private int integer(String value, int defaultValue) {
        if (value == null) return defaultValue;
        try {
            if (!value.matches("[+-]?[0-9]+")) throw new NumberFormatException();
            return Integer.parseInt(value);
        } catch (NumberFormatException ex) { throw new AppException(ErrorCode.ASSIGNMENT_INVALID_REQUEST); }
    }

    private <T> ResponseEntity<ApiResponse<T>> response(SuccessCode code, T data) {
        return ResponseEntity.status(code.getStatus()).body(ApiResponse.of(code, data));
    }
}
