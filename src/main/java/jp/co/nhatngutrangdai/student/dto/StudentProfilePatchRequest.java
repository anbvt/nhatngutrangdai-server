package jp.co.nhatngutrangdai.student.dto;

import com.fasterxml.jackson.annotation.JsonSetter;
import jakarta.validation.constraints.Size;

public class StudentProfilePatchRequest {

    @Size(max = 40)
    private String phone;
    private boolean phonePresent;
    @Size(max = 500)
    private String address;
    private boolean addressPresent;

    @JsonSetter("phone")
    public void setPhone(String value) { phonePresent = true; phone = value; }
    @JsonSetter("address")
    public void setAddress(String value) { addressPresent = true; address = value; }

    public String phone() { return phone; }
    public boolean phonePresent() { return phonePresent; }
    public String address() { return address; }
    public boolean addressPresent() { return addressPresent; }
}
