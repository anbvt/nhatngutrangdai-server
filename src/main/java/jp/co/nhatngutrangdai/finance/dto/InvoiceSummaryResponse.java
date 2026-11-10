package jp.co.nhatngutrangdai.finance.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import jp.co.nhatngutrangdai.finance.entity.InvoiceStatus;

public record InvoiceSummaryResponse(
        UUID id,
        String invoiceNumber,
        UUID studentId,
        UUID enrollmentId,
        LocalDate issueDate,
        LocalDate dueDate,
        String currency,
        BigDecimal totalAmount,
        BigDecimal paidAmount,
        BigDecimal balance,
        InvoiceStatus status) {
}
