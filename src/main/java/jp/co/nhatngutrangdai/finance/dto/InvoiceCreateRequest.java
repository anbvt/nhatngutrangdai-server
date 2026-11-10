package jp.co.nhatngutrangdai.finance.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record InvoiceCreateRequest(
        @NotNull UUID studentId,
        UUID enrollmentId,
        LocalDate dueDate,
        @NotBlank @Pattern(regexp = "(?i)[A-Z]{3}") String currency,
        @NotEmpty @Size(max = 50) List<@Valid InvoiceItemRequest> items) {
}
