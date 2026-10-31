package jp.co.nhatngutrangdai.auth.service;

import java.util.Set;
import java.util.UUID;

import jp.co.nhatngutrangdai.auth.entity.RoleCode;

public record AuthenticatedUser(UUID id, String email, Set<RoleCode> roles) {

    public AuthenticatedUser {
        roles = Set.copyOf(roles);
    }
}
