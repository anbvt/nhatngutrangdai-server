package jp.co.nhatngutrangdai.teacher.entity;

import java.util.UUID;

import jp.co.nhatngutrangdai.common.entity.SchoolStatus;
import jp.co.nhatngutrangdai.common.entity.TimestampedEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "teachers")
public class Teacher extends TimestampedEntity {

    @Id
    @GeneratedValue
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "full_name", nullable = false, length = 160)
    private String fullName;

    @Column(name = "email", length = 254)
    private String email;

    @Column(name = "phone", length = 40)
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private SchoolStatus status;

    protected Teacher() {
    }

    public static Teacher create(String fullName, String email, String phone, SchoolStatus status) {
        Teacher teacher = new Teacher();
        teacher.fullName = fullName.trim();
        teacher.email = email;
        teacher.phone = phone;
        teacher.status = status;
        return teacher;
    }

    public UUID getId() { return id; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public SchoolStatus getStatus() { return status; }

    public void setFullName(String fullName) { this.fullName = fullName.trim(); }
    public void setEmail(String email) { this.email = email; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setStatus(SchoolStatus status) { this.status = status; }
}
