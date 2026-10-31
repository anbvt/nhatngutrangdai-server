package jp.co.nhatngutrangdai.common.api;

import java.time.Instant;
import java.util.List;

public record ApiErrorResponse(
        Instant timestamp,
        int status,
        String code,
        String message,
        List<FieldErrorResponse> fieldErrors,
        String path) {

    public ApiErrorResponse {
        fieldErrors = List.copyOf(fieldErrors);
    }
}
