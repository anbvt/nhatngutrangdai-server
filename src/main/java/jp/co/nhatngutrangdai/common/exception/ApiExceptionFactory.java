package jp.co.nhatngutrangdai.common.exception;

import org.springframework.http.HttpStatus;

public final class ApiExceptionFactory {

    private ApiExceptionFactory() {
    }

    public static ApiException notFound(String resource) {
        return new ApiException(HttpStatus.NOT_FOUND, "NOT_FOUND", resource + " not found");
    }

    public static ApiException conflict(String message) {
        return new ApiException(HttpStatus.CONFLICT, "CONFLICT", message);
    }

    public static ApiException badRequest(String message) {
        return new ApiException(HttpStatus.BAD_REQUEST, "INVALID_REQUEST", message);
    }
}
