package jp.co.nhatngutrangdai.auth.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "roles")
public class Role {

    @Id
    @Enumerated(EnumType.STRING)
    @Column(name = "code", nullable = false, length = 30)
    private RoleCode code;

    @Column(name = "display_name", nullable = false, length = 80)
    private String displayName;

    protected Role() {
    }

    public RoleCode getCode() {
        return code;
    }

    public String getDisplayName() {
        return displayName;
    }
}
