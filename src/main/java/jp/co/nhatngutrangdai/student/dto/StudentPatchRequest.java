package jp.co.nhatngutrangdai.student.dto;

import java.time.LocalDate;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonSetter;
import jp.co.nhatngutrangdai.common.entity.SchoolStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public class StudentPatchRequest {

    @Size(max = 40)
    private String studentCode;
    private boolean studentCodePresent;
    @Size(max = 160)
    private String fullName;
    private boolean fullNamePresent;
    private LocalDate dateOfBirth;
    private boolean dateOfBirthPresent;
    @Email
    @Size(max = 254)
    private String email;
    private boolean emailPresent;
    @Size(max = 40)
    private String phone;
    private boolean phonePresent;
    @Size(max = 500)
    private String address;
    private boolean addressPresent;
    @Size(max = 100)
    private String nationality;
    private boolean nationalityPresent;
    private SchoolStatus status;
    private boolean statusPresent;
    private UUID userId;
    private boolean userIdPresent;

    @JsonSetter("studentCode")
    public void setStudentCode(String value) { studentCodePresent = true; studentCode = value; }
    @JsonSetter("fullName")
    public void setFullName(String value) { fullNamePresent = true; fullName = value; }
    @JsonSetter("dateOfBirth")
    public void setDateOfBirth(LocalDate value) { dateOfBirthPresent = true; dateOfBirth = value; }
    @JsonSetter("email")
    public void setEmail(String value) { emailPresent = true; email = value; }
    @JsonSetter("phone")
    public void setPhone(String value) { phonePresent = true; phone = value; }
    @JsonSetter("address")
    public void setAddress(String value) { addressPresent = true; address = value; }
    @JsonSetter("nationality")
    public void setNationality(String value) { nationalityPresent = true; nationality = value; }
    @JsonSetter("status")
    public void setStatus(SchoolStatus value) { statusPresent = true; status = value; }
    @JsonSetter("userId")
    public void setUserId(UUID value) { userIdPresent = true; userId = value; }

    public String studentCode() { return studentCode; }
    public boolean studentCodePresent() { return studentCodePresent; }
    public String fullName() { return fullName; }
    public boolean fullNamePresent() { return fullNamePresent; }
    public LocalDate dateOfBirth() { return dateOfBirth; }
    public boolean dateOfBirthPresent() { return dateOfBirthPresent; }
    public String email() { return email; }
    public boolean emailPresent() { return emailPresent; }
    public String phone() { return phone; }
    public boolean phonePresent() { return phonePresent; }
    public String address() { return address; }
    public boolean addressPresent() { return addressPresent; }
    public String nationality() { return nationality; }
    public boolean nationalityPresent() { return nationalityPresent; }
    public SchoolStatus status() { return status; }
    public boolean statusPresent() { return statusPresent; }
    public UUID userId() { return userId; }
    public boolean userIdPresent() { return userIdPresent; }
}
