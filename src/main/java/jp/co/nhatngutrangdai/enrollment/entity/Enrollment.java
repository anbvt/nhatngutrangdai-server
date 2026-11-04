package jp.co.nhatngutrangdai.enrollment.entity;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import jp.co.nhatngutrangdai.common.entity.TimestampedEntity;
import jp.co.nhatngutrangdai.course.entity.Course;
import jp.co.nhatngutrangdai.student.entity.Student;
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
@Table(name = "enrollments")
public class Enrollment extends TimestampedEntity {

    @Id
    @GeneratedValue
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id")
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Column(name = "application_reference_hash", nullable = false, length = 64)
    private String applicationReferenceHash;

    @Column(name = "applicant_full_name", nullable = false, length = 160)
    private String applicantFullName;

    @Column(name = "applicant_email", nullable = false, length = 254)
    private String applicantEmail;

    @Column(name = "applicant_email_normalized", nullable = false, length = 254)
    private String applicantEmailNormalized;

    @Column(name = "applicant_phone", nullable = false, length = 40)
    private String applicantPhone;

    @Column(name = "applicant_date_of_birth")
    private LocalDate applicantDateOfBirth;

    @Column(name = "applicant_address", length = 500)
    private String applicantAddress;

    @Column(name = "applicant_nationality", length = 100)
    private String applicantNationality;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private EnrollmentStatus status;

    @Column(name = "pending_duplicate_key", length = 320)
    private String pendingDuplicateKey;

    @Column(name = "submitted_at", nullable = false)
    private Instant submittedAt;

    protected Enrollment() {
    }

    public static Enrollment pendingApplication(
            Course course,
            String applicationReferenceHash,
            String applicantFullName,
            String applicantEmail,
            String applicantEmailNormalized,
            String applicantPhone,
            LocalDate applicantDateOfBirth,
            String applicantAddress,
            String applicantNationality,
            String pendingDuplicateKey,
            Instant submittedAt) {
        Enrollment enrollment = new Enrollment();
        enrollment.course = course;
        enrollment.applicationReferenceHash = applicationReferenceHash;
        enrollment.applicantFullName = applicantFullName.trim();
        enrollment.applicantEmail = applicantEmail.trim();
        enrollment.applicantEmailNormalized = applicantEmailNormalized;
        enrollment.applicantPhone = applicantPhone.trim();
        enrollment.applicantDateOfBirth = applicantDateOfBirth;
        enrollment.applicantAddress = applicantAddress;
        enrollment.applicantNationality = applicantNationality;
        enrollment.status = EnrollmentStatus.PENDING;
        enrollment.pendingDuplicateKey = pendingDuplicateKey;
        enrollment.submittedAt = submittedAt;
        return enrollment;
    }

    public UUID getId() {
        return id;
    }

    public Student getStudent() {
        return student;
    }

    public Course getCourse() {
        return course;
    }

    public String getApplicantFullName() {
        return applicantFullName;
    }

    public String getApplicantEmail() {
        return applicantEmail;
    }

    public String getApplicantPhone() {
        return applicantPhone;
    }

    public LocalDate getApplicantDateOfBirth() {
        return applicantDateOfBirth;
    }

    public String getApplicantAddress() {
        return applicantAddress;
    }

    public String getApplicantNationality() {
        return applicantNationality;
    }

    public EnrollmentStatus getStatus() {
        return status;
    }

    public Instant getSubmittedAt() {
        return submittedAt;
    }
}
