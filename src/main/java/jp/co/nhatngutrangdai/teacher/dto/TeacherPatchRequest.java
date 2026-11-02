package jp.co.nhatngutrangdai.teacher.dto;

import com.fasterxml.jackson.annotation.JsonSetter;
import jp.co.nhatngutrangdai.common.entity.SchoolStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public class TeacherPatchRequest {

    @Size(max = 160)
    private String fullName;
    private boolean fullNamePresent;
    @Email
    @Size(max = 254)
    private String email;
    private boolean emailPresent;
    @Size(max = 40)
    private String phone;
    private boolean phonePresent;
    private SchoolStatus status;
    private boolean statusPresent;

    @JsonSetter("fullName")
    public void setFullName(String value) { fullNamePresent = true; fullName = value; }
    @JsonSetter("email")
    public void setEmail(String value) { emailPresent = true; email = value; }
    @JsonSetter("phone")
    public void setPhone(String value) { phonePresent = true; phone = value; }
    @JsonSetter("status")
    public void setStatus(SchoolStatus value) { statusPresent = true; status = value; }

    public String fullName() { return fullName; }
    public boolean fullNamePresent() { return fullNamePresent; }
    public String email() { return email; }
    public boolean emailPresent() { return emailPresent; }
    public String phone() { return phone; }
    public boolean phonePresent() { return phonePresent; }
    public SchoolStatus status() { return status; }
    public boolean statusPresent() { return statusPresent; }
}
