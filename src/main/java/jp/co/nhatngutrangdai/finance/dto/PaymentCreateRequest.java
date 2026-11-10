package jp.co.nhatngutrangdai.finance.dto;

import java.math.BigDecimal;
import java.util.UUID;

import jp.co.nhatngutrangdai.finance.entity.PaymentMethod;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record PaymentCreateRequest(
        @NotNull UUID invoiceId,
        @NotNull @DecimalMin("0.01") @Digits(integer = 10, fraction = 2) BigDecimal amount,
        @NotNull PaymentMethod method,
        @Size(max = 120) String externalReference,
        @Size(max = 1000) String note) {
}
