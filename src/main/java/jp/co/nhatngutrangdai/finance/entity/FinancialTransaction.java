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
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "financial_transactions")
public class FinancialTransaction {

    @Id
    @GeneratedValue
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "payment_id", nullable = false, unique = true, updatable = false)
    private Payment payment;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false, length = 20, updatable = false)
    private FinancialTransactionType transactionType;

    @Column(name = "amount", nullable = false, precision = 12, scale = 2, updatable = false)
    private BigDecimal amount;

    @Column(name = "currency", nullable = false, length = 3, updatable = false)
    private String currency;

    @Column(name = "occurred_at", nullable = false, updatable = false)
    private Instant occurredAt;

    @JoinColumn(name = "actor_id", nullable = false, updatable = false)
    @jakarta.persistence.ManyToOne(fetch = FetchType.LAZY, optional = false)
    private UserAccount actor;

    @Column(name = "reference", length = 120, updatable = false)
    private String reference;

    protected FinancialTransaction() {
    }

    public static FinancialTransaction forPayment(Payment payment, UserAccount actor, Instant occurredAt) {
        FinancialTransaction transaction = new FinancialTransaction();
        transaction.payment = payment;
        transaction.transactionType = FinancialTransactionType.PAYMENT;
        transaction.amount = payment.getAmount();
        transaction.currency = payment.getCurrency();
        transaction.occurredAt = occurredAt;
        transaction.actor = actor;
        transaction.reference = payment.getExternalReference();
        return transaction;
    }

    public UUID getId() { return id; }
    public Payment getPayment() { return payment; }
    public FinancialTransactionType getTransactionType() { return transactionType; }
    public BigDecimal getAmount() { return amount; }
    public String getCurrency() { return currency; }
    public Instant getOccurredAt() { return occurredAt; }
    public UserAccount getActor() { return actor; }
    public String getReference() { return reference; }
}
