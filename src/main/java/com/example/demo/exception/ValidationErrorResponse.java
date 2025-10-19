package com.example.demo.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ValidationErrorResponse {
    private final LocalDateTime timestamp = LocalDateTime.now();
    private final int status;
    private final String error;
    private final String message;
    private final List<ValidationErrorDetail> errors;

    public ValidationErrorResponse(int status, String error, String message, List<ValidationErrorDetail> errors) {
        this.status = status;
        this.error = error;
        this.message = message;
        this.errors = errors;
    }

    @Getter
    @AllArgsConstructor
    public static class ValidationErrorDetail {
        private final String field;
        private final String message;
    }
}
