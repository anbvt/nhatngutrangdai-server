package jp.co.nhatngutrangdai.finance.entity;

import java.time.Instant;
import java.util.UUID;

import jp.co.nhatngutrangdai.auth.entity.UserAccount;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "financial_audit_events")
public class FinancialAuditEvent {

    @Id
    @GeneratedValue
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "actor_id", nullable = false)
    private UserAccount actor;

    @Column(name = "action", nullable = false, length = 40)
    private String action;

    @Column(name = "resource_type", nullable = false, length = 30)
    private String resourceType;

    @Column(name = "resource_id", nullable = false)
    private UUID resourceId;

    @Column(name = "occurred_at", nullable = false)
    private Instant occurredAt;

    protected FinancialAuditEvent() {
    }

    public static FinancialAuditEvent record(
            UserAccount actor,
            String action,
            String resourceType,
            UUID resourceId,
            Instant occurredAt) {
        FinancialAuditEvent event = new FinancialAuditEvent();
        event.actor = actor;
        event.action = action;
        event.resourceType = resourceType;
        event.resourceId = resourceId;
        event.occurredAt = occurredAt;
        return event;
    }
}
