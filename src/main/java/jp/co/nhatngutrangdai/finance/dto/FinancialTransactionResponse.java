package jp.co.nhatngutrangdai.finance.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import jp.co.nhatngutrangdai.finance.entity.FinancialTransactionType;

public record FinancialTransactionResponse(
        UUID id,
        UUID paymentId,
        FinancialTransactionType transactionType,
        BigDecimal amount,
        String currency,
        Instant occurredAt,
        UUID actorId,
        String reference) {
}
