package jp.co.nhatngutrangdai.finance.entity;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import jp.co.nhatngutrangdai.auth.entity.UserAccount;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "invoice_id", nullable = false)
    private Invoice invoice;

    @Column(name = "amount", nullable = false, precision = 12, scale = 2, updatable = false)
    private BigDecimal amount;

    @Column(name = "currency", nullable = false, length = 3, updatable = false)
    private String currency;

    @Enumerated(EnumType.STRING)
    @Column(name = "method", nullable = false, length = 30, updatable = false)
    private PaymentMethod method;

    @Column(name = "paid_at", nullable = false, updatable = false)
    private Instant paidAt;

    @Column(name = "external_reference", length = 120, updatable = false)
    private String externalReference;

    @Column(name = "note", length = 1000, updatable = false)
    private String note;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "recorded_by", nullable = false, updatable = false)
    private UserAccount recordedBy;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected Payment() {
    }

    public static Payment record(
            Invoice invoice,
            BigDecimal amount,
            PaymentMethod method,
            Instant paidAt,
            String externalReference,
            String note,
            UserAccount recordedBy) {
        Payment payment = new Payment();
        payment.invoice = invoice;
        payment.amount = amount;
        payment.currency = invoice.getCurrency();
        payment.method = method;
        payment.paidAt = paidAt;
        payment.externalReference = externalReference == null || externalReference.isBlank()
                ? null : externalReference.trim();
        payment.note = note;
        payment.recordedBy = recordedBy;
        payment.createdAt = paidAt;
        return payment;
    }

    public UUID getId() { return id; }
    public Invoice getInvoice() { return invoice; }
    public BigDecimal getAmount() { return amount; }
    public String getCurrency() { return currency; }
    public PaymentMethod getMethod() { return method; }
    public Instant getPaidAt() { return paidAt; }
    public String getExternalReference() { return externalReference; }
    public String getNote() { return note; }
    public UserAccount getRecordedBy() { return recordedBy; }
}
