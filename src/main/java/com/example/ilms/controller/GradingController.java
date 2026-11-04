package com.example.ilms.controller;

import com.example.ilms.constant.SuccessCode;
import com.example.ilms.dto.common.BatchOperationResponseDTO;
import com.example.ilms.dto.grading.*;
import com.example.ilms.dto.response.ApiResponse;
import com.example.ilms.dto.response.ErrorResponse;
import com.example.ilms.dto.submission.SubmissionJsonDeserializer;
import com.example.ilms.entity.User;
import com.example.ilms.exception.AppException;
import com.example.ilms.exception.ErrorCode;
import com.example.ilms.service.GradingService;
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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Slf4j
public class GradingController {
    private final GradingService gradingService;

    @PostMapping(value = "/submissions/{submissionId}/grading", consumes = "application/json")
    public ResponseEntity<ApiResponse<GradingResponseDTO>> createGrading(@PathVariable("submissionId") String submissionId,
            @RequestBody @Validated(GradingRequestDTO.Create.class) GradingRequestDTO request,
            @AuthenticationPrincipal User user) {
        return response(SuccessCode.CREATE_GRADING_SUCCESS,
                gradingService.createGrading(id(submissionId), request, user));
    }

    @GetMapping("/submissions/{submissionId}/grading")
    public ResponseEntity<ApiResponse<GradingResponseDTO>> getSubmissionGrading(
            @PathVariable("submissionId") String submissionId, @AuthenticationPrincipal User user) {
        return response(SuccessCode.GET_GRADING_SUCCESS, gradingService.getSubmissionGrading(id(submissionId), user));
    }

    @GetMapping("/gradings/{id}")
    public ResponseEntity<ApiResponse<GradingResponseDTO>> getGrading(
            @PathVariable("id") String id, @AuthenticationPrincipal User user) {
        return response(SuccessCode.GET_GRADING_SUCCESS, gradingService.getGrading(id(id), user));
    }

    @RequestMapping(value = "/gradings/{id}", method = {RequestMethod.PUT, RequestMethod.PATCH}, consumes = "application/json")
    public ResponseEntity<ApiResponse<GradingResponseDTO>> updateGrading(@PathVariable("id") String id,
            @RequestBody @Valid GradingRequestDTO request, @AuthenticationPrincipal User user) {
        return response(SuccessCode.UPDATE_GRADING_SUCCESS, gradingService.updateGrading(id(id), request, user));
    }

    @PostMapping(value = "/gradings/batch", consumes = "application/json")
    public ResponseEntity<ApiResponse<BatchOperationResponseDTO>> batchCreateGradings(
            @RequestBody @Valid BatchCreateGradingsRequestDTO request, @AuthenticationPrincipal User user) {
        BatchOperationResponseDTO result = gradingService.batchCreateGradings(request, user);
        return response(result.getFailedCount() == 0 ? SuccessCode.BATCH_CREATE_GRADINGS_SUCCESS
                : SuccessCode.BATCH_CREATE_GRADINGS_PARTIAL_SUCCESS, result);
    }

    @DeleteMapping("/gradings/{id}")
    public ResponseEntity<Void> deleteGrading(@PathVariable("id") String id, @AuthenticationPrincipal User user) {
        gradingService.deleteGrading(id(id), user);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> invalidJson(HttpMessageNotReadableException ex) {
        return error(ErrorCode.GRADING_INVALID_REQUEST);
    }

    @ExceptionHandler({DataAccessException.class, TransactionException.class})
    public ResponseEntity<ErrorResponse> persistenceFailure(RuntimeException ex) {
        log.error("Grading persistence operation failed", ex);
        if (ex instanceof ConcurrencyFailureException || ex instanceof DataIntegrityViolationException
                || ex instanceof UnexpectedRollbackException) return error(ErrorCode.GRADING_WRITE_CONFLICT);
        return ResponseEntity.internalServerError().body(ErrorResponse.builder().status(500)
                .message("Không thể truy cập dữ liệu chấm điểm").timestamp(LocalDateTime.now()).build());
    }

    private UUID id(String value) {
        try { return SubmissionJsonDeserializer.parseId(value); }
        catch (IllegalArgumentException ex) { throw new AppException(ErrorCode.GRADING_INVALID_REQUEST); }
    }

    private ResponseEntity<ErrorResponse> error(ErrorCode code) {
        return ResponseEntity.status(code.getStatus()).body(ErrorResponse.builder().status(code.getStatus().value())
                .message(code.getMessage()).timestamp(LocalDateTime.now()).build());
    }

    private <T> ResponseEntity<ApiResponse<T>> response(SuccessCode code, T data) {
        return ResponseEntity.status(code.getStatus()).body(ApiResponse.of(code, data));
    }
}
