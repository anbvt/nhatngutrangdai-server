package jp.co.nhatngutrangdai.student.entity;

import java.time.LocalDate;
import java.util.UUID;

import jp.co.nhatngutrangdai.auth.entity.UserAccount;
import jp.co.nhatngutrangdai.common.entity.SchoolStatus;
import jp.co.nhatngutrangdai.common.entity.TimestampedEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "students")
public class Student extends TimestampedEntity {

    @Id
    @GeneratedValue
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @OneToOne
    @JoinColumn(name = "user_id", unique = true)
    private UserAccount userAccount;

    @Column(name = "student_code", nullable = false, length = 40)
    private String studentCode;

    @Column(name = "full_name", nullable = false, length = 160)
    private String fullName;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "email", length = 254)
    private String email;

    @Column(name = "phone", length = 40)
    private String phone;

    @Column(name = "address", length = 500)
    private String address;

    @Column(name = "nationality", length = 100)
    private String nationality;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private SchoolStatus status;

    protected Student() {
    }

    public static Student create(
            String studentCode,
            String fullName,
            LocalDate dateOfBirth,
            String email,
            String phone,
            String address,
            String nationality,
            SchoolStatus status,
            UserAccount userAccount) {
        Student student = new Student();
        student.studentCode = studentCode.trim();
        student.fullName = fullName.trim();
        student.dateOfBirth = dateOfBirth;
        student.email = email;
        student.phone = phone;
        student.address = address;
        student.nationality = nationality;
        student.status = status;
        student.userAccount = userAccount;
        return student;
    }

    public UUID getId() {
        return id;
    }

    public UserAccount getUserAccount() {
        return userAccount;
    }

    public String getStudentCode() {
        return studentCode;
    }

    public String getFullName() {
        return fullName;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getAddress() {
        return address;
    }

    public String getNationality() {
        return nationality;
    }

    public SchoolStatus getStatus() {
        return status;
    }

    public void setStudentCode(String studentCode) {
        this.studentCode = studentCode.trim();
    }

    public void setFullName(String fullName) {
        this.fullName = fullName.trim();
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public void setStatus(SchoolStatus status) {
        this.status = status;
    }

    public void setUserAccount(UserAccount userAccount) {
        this.userAccount = userAccount;
    }
}
