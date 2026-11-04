package com.example.ilms.controller;

import com.example.ilms.constant.SuccessCode;
import com.example.ilms.dto.common.BatchOperationResponseDTO;
import com.example.ilms.dto.common.PageResponse;
import com.example.ilms.dto.response.ApiResponse;
import com.example.ilms.dto.response.ErrorResponse;
import com.example.ilms.dto.submission.*;
import com.example.ilms.entity.User;
import com.example.ilms.exception.AppException;
import com.example.ilms.exception.ErrorCode;
import com.example.ilms.service.SubmissionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.ConcurrencyFailureException;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Slf4j
public class SubmissionController {
    private final SubmissionService submissionService;

    @GetMapping("/assignments/{assignmentId}/submissions")
    public ResponseEntity<ApiResponse<PageResponse<SubmissionSummaryResponseDTO>>> getSubmissions(
            @PathVariable String assignmentId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String page,
            @RequestParam(required = false) String size,
            @RequestParam(required = false) String sort,
            @AuthenticationPrincipal User user) {
        return response(SuccessCode.GET_SUBMISSIONS_SUCCESS, submissionService.getSubmissions(
                id(assignmentId), status, search, integer(page, 0), integer(size, 10), sort, user));
    }

    @GetMapping("/assignments/{assignmentId}/my-submission")
    public ResponseEntity<ApiResponse<SubmissionDetailResponseDTO>> getMySubmission(
            @PathVariable String assignmentId, @AuthenticationPrincipal User user) {
        return response(SuccessCode.GET_SUBMISSION_SUCCESS, submissionService.getMySubmission(id(assignmentId), user));
    }

    @GetMapping("/submissions/{id}")
    public ResponseEntity<ApiResponse<SubmissionDetailResponseDTO>> getSubmission(
            @PathVariable String id, @AuthenticationPrincipal User user) {
        return response(SuccessCode.GET_SUBMISSION_SUCCESS, submissionService.getSubmission(id(id), user));
    }

    @PostMapping(value = "/assignments/{assignmentId}/submissions", consumes = "application/json")
    public ResponseEntity<ApiResponse<SubmissionDetailResponseDTO>> createSubmission(@PathVariable String assignmentId,
            @RequestBody @Valid SubmissionRequestDTO request, @AuthenticationPrincipal User user) {
        return response(SuccessCode.CREATE_SUBMISSION_SUCCESS, submissionService.createSubmission(id(assignmentId), request, user));
    }

    @RequestMapping(value = "/assignments/{assignmentId}/submissions", method = {RequestMethod.PUT, RequestMethod.PATCH},
            consumes = "application/json")
    public ResponseEntity<ApiResponse<SubmissionDetailResponseDTO>> updateSubmission(@PathVariable String assignmentId,
            @RequestBody @Valid SubmissionRequestDTO request, @AuthenticationPrincipal User user) {
        return response(SuccessCode.UPDATE_SUBMISSION_SUCCESS, submissionService.updateSubmission(id(assignmentId), request, user));
    }

    @PatchMapping(value = "/submissions/{id}/grade", consumes = "application/json")
    public ResponseEntity<ApiResponse<SubmissionDetailResponseDTO>> gradeSubmission(@PathVariable String id,
            @RequestBody @Valid SubmissionGradeRequestDTO request, @AuthenticationPrincipal User user) {
        return response(SuccessCode.GRADE_SUBMISSION_SUCCESS, submissionService.gradeSubmission(id(id), request, user));
    }

    @PostMapping(value = "/assignments/{assignmentId}/submissions/grade/batch", consumes = "application/json")
    public ResponseEntity<ApiResponse<BatchOperationResponseDTO>> batchGradeSubmissions(@PathVariable String assignmentId,
            @RequestBody @Valid BatchGradeSubmissionsRequestDTO request, @AuthenticationPrincipal User user) {
        BatchOperationResponseDTO result = submissionService.batchGradeSubmissions(id(assignmentId), request, user);
        return response(result.getFailedCount() == 0 ? SuccessCode.BATCH_GRADE_SUBMISSIONS_SUCCESS
                : SuccessCode.BATCH_GRADE_SUBMISSIONS_PARTIAL_SUCCESS, result);
    }

    @DeleteMapping("/submissions/{id}")
    public ResponseEntity<Void> deleteSubmission(@PathVariable String id, @AuthenticationPrincipal User user) {
        submissionService.deleteSubmission(id(id), user);
        return ResponseEntity.noContent().build();
    }

    // These handlers belong only to Submission; shared Security/Jackson/error behavior is unchanged.
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> invalidJson(HttpMessageNotReadableException ex) {
        return error(ErrorCode.SUBMISSION_INVALID_REQUEST);
    }

    @ExceptionHandler({DataAccessException.class, TransactionException.class})
    public ResponseEntity<ErrorResponse> persistenceFailure(RuntimeException ex) {
        log.error("Submission persistence operation failed", ex);
        if (ex instanceof ConcurrencyFailureException || ex instanceof DataIntegrityViolationException
                || ex instanceof UnexpectedRollbackException) {
            return error(ErrorCode.SUBMISSION_WRITE_CONFLICT);
        }
        return ResponseEntity.internalServerError().body(ErrorResponse.builder().status(500)
                .message("Không thể truy cập dữ liệu bài nộp").timestamp(LocalDateTime.now()).build());
    }

    private ResponseEntity<ErrorResponse> error(ErrorCode code) {
        return ResponseEntity.status(code.getStatus()).body(ErrorResponse.builder()
                .status(code.getStatus().value()).message(code.getMessage()).timestamp(LocalDateTime.now()).build());
    }

    private UUID id(String value) {
        try {
            return SubmissionJsonDeserializer.parseId(value);
        } catch (IllegalArgumentException ex) {
            throw new AppException(ErrorCode.SUBMISSION_INVALID_REQUEST);
        }
    }

    private int integer(String value, int defaultValue) {
        if (value == null) return defaultValue;
        try {
            if (!value.matches("[+-]?[0-9]+")) throw new NumberFormatException();
            return Integer.parseInt(value);
        } catch (NumberFormatException ex) {
            throw new AppException(ErrorCode.SUBMISSION_INVALID_REQUEST);
        }
    }

    private <T> ResponseEntity<ApiResponse<T>> response(SuccessCode code, T data) {
        return ResponseEntity.status(code.getStatus()).body(ApiResponse.of(code, data));
    }
}
