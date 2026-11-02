package jp.co.nhatngutrangdai.common.exception;

import java.time.Instant;
import java.util.List;

import jp.co.nhatngutrangdai.common.api.ApiErrorResponse;
import jp.co.nhatngutrangdai.common.api.FieldErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidation(
            MethodArgumentNotValidException exception,
            HttpServletRequest request) {
        List<FieldErrorResponse> fieldErrors = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> new FieldErrorResponse(
                        error.getField(),
                        error.getDefaultMessage() == null ? "Invalid value" : error.getDefaultMessage()))
                .toList();

        return errorResponse(
                HttpStatus.BAD_REQUEST,
                "VALIDATION_ERROR",
                "Request validation failed",
                fieldErrors,
                request);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorResponse> handleUnreadableMessage(
            HttpMessageNotReadableException exception,
            HttpServletRequest request) {
        return errorResponse(
                HttpStatus.BAD_REQUEST,
                "MALFORMED_REQUEST",
                "Request body is invalid",
                List.of(),
                request);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiErrorResponse> handleInvalidParameter(
            MethodArgumentTypeMismatchException exception,
            HttpServletRequest request) {
        return errorResponse(
                HttpStatus.BAD_REQUEST,
                "INVALID_PARAMETER",
                "Request parameter is invalid",
                List.of(new FieldErrorResponse(exception.getName(), "Invalid value")),
                request);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNotFound(
            NoResourceFoundException exception,
            HttpServletRequest request) {
        return errorResponse(
                HttpStatus.NOT_FOUND,
                "NOT_FOUND",
                "Resource not found",
                List.of(),
                request);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiErrorResponse> handleMethodNotAllowed(
            HttpRequestMethodNotSupportedException exception,
            HttpServletRequest request) {
        return errorResponse(
                HttpStatus.METHOD_NOT_ALLOWED,
                "METHOD_NOT_ALLOWED",
                "HTTP method is not supported for this resource",
                List.of(),
                request);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiErrorResponse> handleBadCredentials(
            BadCredentialsException exception,
            HttpServletRequest request) {
        return errorResponse(
                HttpStatus.UNAUTHORIZED,
                "INVALID_CREDENTIALS",
                "Email or password is incorrect",
                List.of(),
                request);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiErrorResponse> handleAccessDenied(
            AccessDeniedException exception,
            HttpServletRequest request) {
        return errorResponse(
                HttpStatus.FORBIDDEN,
                "FORBIDDEN",
                "You are not allowed to access this resource",
                List.of(),
                request);
    }

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiErrorResponse> handleApiException(
            ApiException exception,
            HttpServletRequest request) {
        return errorResponse(
                exception.getStatus(),
                exception.getCode(),
                exception.getMessage(),
                List.of(),
                request);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleDataConflict(
            DataIntegrityViolationException exception,
            HttpServletRequest request) {
        return errorResponse(
                HttpStatus.CONFLICT,
                "CONFLICT",
                "The request conflicts with existing data",
                List.of(),
                request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleUnexpected(
            Exception exception,
            HttpServletRequest request) {
        log.error("Unhandled API exception type={}", exception.getClass().getName());
        return errorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "INTERNAL_SERVER_ERROR",
                "An unexpected error occurred",
                List.of(),
                request);
    }

    private ResponseEntity<ApiErrorResponse> errorResponse(
            HttpStatus status,
            String code,
            String message,
            List<FieldErrorResponse> fieldErrors,
            HttpServletRequest request) {
        ApiErrorResponse body = new ApiErrorResponse(
                Instant.now(),
                status.value(),
                code,
                message,
                fieldErrors,
                request.getRequestURI());

        return ResponseEntity.status(status).body(body);
    }
}
