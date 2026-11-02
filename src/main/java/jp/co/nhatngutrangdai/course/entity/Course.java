package jp.co.nhatngutrangdai.course.entity;

import java.math.BigDecimal;
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
@Table(name = "courses")
public class Course extends TimestampedEntity {

    @Id
    @GeneratedValue
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "code", nullable = false, length = 40)
    private String code;

    @Column(name = "name", nullable = false, length = 160)
    private String name;

    @Column(name = "description", length = 2000)
    private String description;

    @Column(name = "level", length = 80)
    private String level;

    @Column(name = "duration", length = 120)
    private String duration;

    @Column(name = "tuition_amount", precision = 12, scale = 2)
    private BigDecimal tuitionAmount;

    @Column(name = "currency", length = 3)
    private String currency;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private SchoolStatus status;

    protected Course() {
    }

    public static Course create(
            String code,
            String name,
            String description,
            String level,
            String duration,
            BigDecimal tuitionAmount,
            String currency,
            SchoolStatus status) {
        Course course = new Course();
        course.code = code.trim();
        course.name = name.trim();
        course.description = description;
        course.level = level;
        course.duration = duration;
        course.tuitionAmount = tuitionAmount;
        course.currency = normalizeCurrency(currency);
        course.status = status;
        return course;
    }

    public UUID getId() { return id; }
    public String getCode() { return code; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getLevel() { return level; }
    public String getDuration() { return duration; }
    public BigDecimal getTuitionAmount() { return tuitionAmount; }
    public String getCurrency() { return currency; }
    public SchoolStatus getStatus() { return status; }

    public void setCode(String code) { this.code = code.trim(); }
    public void setName(String name) { this.name = name.trim(); }
    public void setDescription(String description) { this.description = description; }
    public void setLevel(String level) { this.level = level; }
    public void setDuration(String duration) { this.duration = duration; }
    public void setTuitionAmount(BigDecimal tuitionAmount) { this.tuitionAmount = tuitionAmount; }
    public void setCurrency(String currency) { this.currency = normalizeCurrency(currency); }
    public void setStatus(SchoolStatus status) { this.status = status; }

    private static String normalizeCurrency(String currency) {
        return currency == null ? null : currency.toUpperCase(java.util.Locale.ROOT);
    }
}
