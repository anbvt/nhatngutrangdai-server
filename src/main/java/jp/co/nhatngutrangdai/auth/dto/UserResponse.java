package jp.co.nhatngutrangdai.auth.dto;

import java.util.List;
import java.util.UUID;

import jp.co.nhatngutrangdai.auth.entity.RoleCode;

public record UserResponse(UUID id, String email, List<RoleCode> roles) {
}
