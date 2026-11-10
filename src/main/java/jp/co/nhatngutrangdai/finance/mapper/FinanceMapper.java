package jp.co.nhatngutrangdai.finance.mapper;

import java.math.BigDecimal;

import jp.co.nhatngutrangdai.finance.dto.FinancialTransactionResponse;
import jp.co.nhatngutrangdai.finance.dto.InvoiceDetailResponse;
import jp.co.nhatngutrangdai.finance.dto.InvoiceItemResponse;
import jp.co.nhatngutrangdai.finance.dto.InvoiceSummaryResponse;
import jp.co.nhatngutrangdai.finance.dto.PaymentResponse;
import jp.co.nhatngutrangdai.finance.entity.FinancialTransaction;
import jp.co.nhatngutrangdai.finance.entity.Invoice;
import jp.co.nhatngutrangdai.finance.entity.InvoiceStatus;
import jp.co.nhatngutrangdai.finance.entity.Payment;
import org.springframework.stereotype.Component;

@Component
public class FinanceMapper {

    public InvoiceSummaryResponse toSummary(Invoice invoice, BigDecimal paidAmount) {
        BigDecimal normalizedPaidAmount = paidAmount == null ? BigDecimal.ZERO : paidAmount;
        return new InvoiceSummaryResponse(
                invoice.getId(),
                invoice.getInvoiceNumber(),
                invoice.getStudent().getId(),
                invoice.getEnrollment() == null ? null : invoice.getEnrollment().getId(),
                invoice.getIssueDate(),
                invoice.getDueDate(),
                invoice.getCurrency(),
                invoice.getTotalAmount(),
                normalizedPaidAmount,
                invoice.getTotalAmount().subtract(normalizedPaidAmount),
                status(invoice.getTotalAmount(), normalizedPaidAmount));
    }

    public InvoiceDetailResponse toDetail(Invoice invoice, BigDecimal paidAmount) {
        InvoiceSummaryResponse summary = toSummary(invoice, paidAmount);
        return new InvoiceDetailResponse(
                summary.id(),
                summary.invoiceNumber(),
                summary.studentId(),
                summary.enrollmentId(),
                summary.issueDate(),
                summary.dueDate(),
                summary.currency(),
                summary.totalAmount(),
                summary.paidAmount(),
                summary.balance(),
                summary.status(),
                invoice.getItems().stream()
                        .map(item -> new InvoiceItemResponse(
                                item.getId(),
                                item.getLineNumber(),
                                item.getDescription(),
                                item.getQuantity(),
                                item.getUnitAmount(),
                                item.getLineTotal()))
                        .toList());
    }

    public PaymentResponse toPayment(Payment payment) {
        return new PaymentResponse(
                payment.getId(),
                payment.getInvoice().getId(),
                payment.getInvoice().getInvoiceNumber(),
                payment.getInvoice().getStudent().getId(),
                payment.getAmount(),
                payment.getCurrency(),
                payment.getMethod(),
                payment.getPaidAt(),
                payment.getExternalReference(),
                payment.getNote(),
                payment.getRecordedBy().getId());
    }

    public FinancialTransactionResponse toTransaction(FinancialTransaction transaction) {
        return new FinancialTransactionResponse(
                transaction.getId(),
                transaction.getPayment().getId(),
                transaction.getTransactionType(),
                transaction.getAmount(),
                transaction.getCurrency(),
                transaction.getOccurredAt(),
                transaction.getActor().getId(),
                transaction.getReference());
    }

    private InvoiceStatus status(BigDecimal totalAmount, BigDecimal paidAmount) {
        if (paidAmount.compareTo(totalAmount) >= 0) {
            return InvoiceStatus.PAID;
        }
        if (paidAmount.signum() > 0) {
            return InvoiceStatus.PARTIALLY_PAID;
        }
        return InvoiceStatus.ISSUED;
    }
}
