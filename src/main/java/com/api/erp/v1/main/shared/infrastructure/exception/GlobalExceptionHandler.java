package com.api.erp.v1.main.shared.infrastructure.exception;

import com.api.erp.v1.main.shared.domain.exception.BusinessException;
import com.api.erp.v1.main.shared.domain.exception.NotFoundException;
import com.api.erp.v1.main.shared.domain.exception.ValidationException;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.nio.file.AccessDeniedException;
import java.util.Collections;

@Hidden
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            ValidationException ex,
            HttpServletRequest request) {

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                "Erro de validação nos dados fornecidos",
                request.getRequestURI()
        );

        ErrorResponse.FieldError fieldError = new ErrorResponse.FieldError(
                ex.getField(),
                ex.getMessage()
        );

        errorResponse.setErrors(Collections.singletonList(fieldError));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
            IllegalArgumentException ex,
            HttpServletRequest request) {

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(
            BusinessException ex,
            HttpServletRequest request) {

        HttpStatus status = ex.getStatus();

        return ResponseEntity
                .status(status)
                .body(new ErrorResponse(
                        status.value(),
                        status.getReasonPhrase(),
                        ex.getMessage(),
                        request.getRequestURI()
                ));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(
            AccessDeniedException ex,
            HttpServletRequest request) {

        HttpStatus status = HttpStatus.FORBIDDEN;

        return ResponseEntity
                .status(status)
                .body(new ErrorResponse(
                        status.value(),
                        status.getReasonPhrase(),
                        ex.getMessage(),
                        request.getRequestURI()
                ));
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundException(
            NotFoundException ex,
            HttpServletRequest request) {

        HttpStatus status = HttpStatus.NOT_FOUND;

        return ResponseEntity
                .status(status)
                .body(new ErrorResponse(
                        status.value(),
                        status.getReasonPhrase(),
                        ex.getMessage(),
                        request.getRequestURI()
                ));
    }

    @ExceptionHandler(DataSourceNotConfiguredException.class)
    public ResponseEntity<ErrorResponse> handleDataSourceNotConfiguredException(
            DataSourceNotConfiguredException ex,
            HttpServletRequest request) {

        HttpStatus status = HttpStatus.SERVICE_UNAVAILABLE;

        return ResponseEntity
                .status(status)
                .body(new ErrorResponse(
                        status.value(),
                        "Database Not Configured",
                        ex.getMessage(),
                        request.getRequestURI()
                ));
    }


}