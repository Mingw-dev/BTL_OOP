package com.example.ilms.dto.response;

import com.example.ilms.constant.SuccessCode;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.time.LocalDateTime;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private int status;
    private String message;
    private T data;

    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();

    public static <T> ApiResponse<T> of(SuccessCode successCode, T data) {
        return ApiResponse.<T>builder()
                .status(successCode.getStatus().value())
                .message(successCode.getMessage())
                .data(data)
                .build();
    }
}