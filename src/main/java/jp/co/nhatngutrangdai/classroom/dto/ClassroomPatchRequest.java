package jp.co.nhatngutrangdai.classroom.dto;

import java.time.LocalDate;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonSetter;
import jp.co.nhatngutrangdai.common.entity.SchoolStatus;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public class ClassroomPatchRequest {

    private UUID courseId;
    private boolean courseIdPresent;
    @Size(max = 40)
    private String code;
    private boolean codePresent;
    @Size(max = 160)
    private String name;
    private boolean namePresent;
    @Positive
    private Integer capacity;
    private boolean capacityPresent;
    private LocalDate startDate;
    private boolean startDatePresent;
    private LocalDate endDate;
    private boolean endDatePresent;
    private SchoolStatus status;
    private boolean statusPresent;

    @JsonSetter("courseId")
    public void setCourseId(UUID value) { courseIdPresent = true; courseId = value; }
    @JsonSetter("code")
    public void setCode(String value) { codePresent = true; code = value; }
    @JsonSetter("name")
    public void setName(String value) { namePresent = true; name = value; }
    @JsonSetter("capacity")
    public void setCapacity(Integer value) { capacityPresent = true; capacity = value; }
    @JsonSetter("startDate")
    public void setStartDate(LocalDate value) { startDatePresent = true; startDate = value; }
    @JsonSetter("endDate")
    public void setEndDate(LocalDate value) { endDatePresent = true; endDate = value; }
    @JsonSetter("status")
    public void setStatus(SchoolStatus value) { statusPresent = true; status = value; }

    public UUID courseId() { return courseId; }
    public boolean courseIdPresent() { return courseIdPresent; }
    public String code() { return code; }
    public boolean codePresent() { return codePresent; }
    public String name() { return name; }
    public boolean namePresent() { return namePresent; }
    public Integer capacity() { return capacity; }
    public boolean capacityPresent() { return capacityPresent; }
    public LocalDate startDate() { return startDate; }
    public boolean startDatePresent() { return startDatePresent; }
    public LocalDate endDate() { return endDate; }
    public boolean endDatePresent() { return endDatePresent; }
    public SchoolStatus status() { return status; }
    public boolean statusPresent() { return statusPresent; }
}
