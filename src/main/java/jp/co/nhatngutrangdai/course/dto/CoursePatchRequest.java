package jp.co.nhatngutrangdai.course.dto;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonSetter;
import jp.co.nhatngutrangdai.common.entity.SchoolStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class CoursePatchRequest {

    @Size(max = 40)
    private String code;
    private boolean codePresent;
    @Size(max = 160)
    private String name;
    private boolean namePresent;
    @Size(max = 2000)
    private String description;
    private boolean descriptionPresent;
    @Size(max = 80)
    private String level;
    private boolean levelPresent;
    @Size(max = 120)
    private String duration;
    private boolean durationPresent;
    @DecimalMin(value = "0.00")
    @Digits(integer = 10, fraction = 2)
    private BigDecimal tuitionAmount;
    private boolean tuitionAmountPresent;
    @Pattern(regexp = "[A-Za-z]{3}")
    private String currency;
    private boolean currencyPresent;
    private SchoolStatus status;
    private boolean statusPresent;

    @JsonSetter("code")
    public void setCode(String value) { codePresent = true; code = value; }
    @JsonSetter("name")
    public void setName(String value) { namePresent = true; name = value; }
    @JsonSetter("description")
    public void setDescription(String value) { descriptionPresent = true; description = value; }
    @JsonSetter("level")
    public void setLevel(String value) { levelPresent = true; level = value; }
    @JsonSetter("duration")
    public void setDuration(String value) { durationPresent = true; duration = value; }
    @JsonSetter("tuitionAmount")
    public void setTuitionAmount(BigDecimal value) { tuitionAmountPresent = true; tuitionAmount = value; }
    @JsonSetter("currency")
    public void setCurrency(String value) { currencyPresent = true; currency = value; }
    @JsonSetter("status")
    public void setStatus(SchoolStatus value) { statusPresent = true; status = value; }

    public String code() { return code; }
    public boolean codePresent() { return codePresent; }
    public String name() { return name; }
    public boolean namePresent() { return namePresent; }
    public String description() { return description; }
    public boolean descriptionPresent() { return descriptionPresent; }
    public String level() { return level; }
    public boolean levelPresent() { return levelPresent; }
    public String duration() { return duration; }
    public boolean durationPresent() { return durationPresent; }
    public BigDecimal tuitionAmount() { return tuitionAmount; }
    public boolean tuitionAmountPresent() { return tuitionAmountPresent; }
    public String currency() { return currency; }
    public boolean currencyPresent() { return currencyPresent; }
    public SchoolStatus status() { return status; }
    public boolean statusPresent() { return statusPresent; }
}
