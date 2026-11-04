package jp.co.nhatngutrangdai.enrollment.entity;

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
@Table(name = "enrollment_status_history")
public class EnrollmentStatusHistory {

    @Id
    @GeneratedValue
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "enrollment_id", nullable = false)
    private Enrollment enrollment;

    @Enumerated(EnumType.STRING)
    @Column(name = "old_status", length = 20)
    private EnrollmentStatus oldStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "new_status", nullable = false, length = 20)
    private EnrollmentStatus newStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "actor_id")
    private UserAccount actor;

    @Column(name = "occurred_at", nullable = false)
    private Instant occurredAt;

    @Column(name = "note", length = 1000)
    private String note;

    protected EnrollmentStatusHistory() {
    }

    public static EnrollmentStatusHistory initialSubmission(Enrollment enrollment, Instant occurredAt) {
        EnrollmentStatusHistory history = new EnrollmentStatusHistory();
        history.enrollment = enrollment;
        history.newStatus = EnrollmentStatus.PENDING;
        history.occurredAt = occurredAt;
        return history;
    }

    public EnrollmentStatus getOldStatus() {
        return oldStatus;
    }

    public EnrollmentStatus getNewStatus() {
        return newStatus;
    }

    public UserAccount getActor() {
        return actor;
    }

    public Instant getOccurredAt() {
        return occurredAt;
    }

    public String getNote() {
        return note;
    }
}
