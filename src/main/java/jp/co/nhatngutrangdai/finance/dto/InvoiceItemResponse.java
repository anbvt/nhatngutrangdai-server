package jp.co.nhatngutrangdai.finance.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record InvoiceItemResponse(
        UUID id,
        int lineNumber,
        String description,
        int quantity,
        BigDecimal unitAmount,
        BigDecimal lineTotal) {
}
