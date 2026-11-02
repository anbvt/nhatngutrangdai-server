package jp.co.nhatngutrangdai.classroom.entity;

import java.time.LocalDate;
import java.util.UUID;

import jp.co.nhatngutrangdai.common.entity.SchoolStatus;
import jp.co.nhatngutrangdai.common.entity.TimestampedEntity;
import jp.co.nhatngutrangdai.course.entity.Course;
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
@Table(name = "classrooms")
public class Classroom extends TimestampedEntity {

    @Id
    @GeneratedValue
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Column(name = "code", nullable = false, length = 40)
    private String code;

    @Column(name = "name", nullable = false, length = 160)
    private String name;

    @Column(name = "capacity", nullable = false)
    private int capacity;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private SchoolStatus status;

    protected Classroom() {
    }

    public static Classroom create(
            Course course,
            String code,
            String name,
            int capacity,
            LocalDate startDate,
            LocalDate endDate,
            SchoolStatus status) {
        Classroom classroom = new Classroom();
        classroom.course = course;
        classroom.code = code.trim();
        classroom.name = name.trim();
        classroom.capacity = capacity;
        classroom.startDate = startDate;
        classroom.endDate = endDate;
        classroom.status = status;
        return classroom;
    }

    public UUID getId() { return id; }
    public Course getCourse() { return course; }
    public String getCode() { return code; }
    public String getName() { return name; }
    public int getCapacity() { return capacity; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
    public SchoolStatus getStatus() { return status; }

    public void setCourse(Course course) { this.course = course; }
    public void setCode(String code) { this.code = code.trim(); }
    public void setName(String name) { this.name = name.trim(); }
    public void setCapacity(int capacity) { this.capacity = capacity; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    public void setStatus(SchoolStatus status) { this.status = status; }
}
