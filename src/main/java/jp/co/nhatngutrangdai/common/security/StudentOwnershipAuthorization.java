package jp.co.nhatngutrangdai.common.security;

import java.util.UUID;

import jp.co.nhatngutrangdai.auth.service.AuthenticatedUser;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class StudentOwnershipAuthorization {

    public boolean isOwner(UUID resourceOwnerUserId, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()
                || !(authentication.getPrincipal() instanceof AuthenticatedUser user)) {
            return false;
        }
        return user.id().equals(resourceOwnerUserId);
    }
}
