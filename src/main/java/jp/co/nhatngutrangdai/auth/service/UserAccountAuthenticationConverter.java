package jp.co.nhatngutrangdai.auth.service;

import java.util.UUID;

import jp.co.nhatngutrangdai.auth.entity.UserStatus;
import jp.co.nhatngutrangdai.auth.repository.UserAccountRepository;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
public class UserAccountAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private final UserAccountRepository userAccountRepository;

    public UserAccountAuthenticationConverter(UserAccountRepository userAccountRepository) {
        this.userAccountRepository = userAccountRepository;
    }

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        UUID userId;
        try {
            String subject = jwt.getSubject();
            if (subject == null) {
                throw new IllegalArgumentException("Token subject is missing");
            }
            userId = UUID.fromString(subject);
        } catch (IllegalArgumentException exception) {
            throw new InvalidBearerTokenException("Invalid bearer token");
        }

        var user = userAccountRepository.findById(userId)
                .filter(account -> account.getStatus() == UserStatus.ACTIVE)
                .orElseThrow(() -> new InvalidBearerTokenException("Invalid bearer token"));

        AuthenticatedUser principal = new AuthenticatedUser(
                user.getId(),
                user.getEmail(),
                user.getRoles().stream().map(role -> role.getCode())
                        .collect(java.util.stream.Collectors.toUnmodifiableSet()));
        var authorities = principal.roles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
                .toList();

        return new UsernamePasswordAuthenticationToken(principal, jwt, authorities);
    }
}
