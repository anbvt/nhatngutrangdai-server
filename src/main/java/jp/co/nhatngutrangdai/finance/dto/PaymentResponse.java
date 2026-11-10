package jp.co.nhatngutrangdai.finance.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import jp.co.nhatngutrangdai.finance.entity.PaymentMethod;

public record PaymentResponse(
        UUID id,
        UUID invoiceId,
        String invoiceNumber,
        UUID studentId,
        BigDecimal amount,
        String currency,
        PaymentMethod method,
        Instant paidAt,
        String externalReference,
        String note,
        UUID recordedBy) {
}
