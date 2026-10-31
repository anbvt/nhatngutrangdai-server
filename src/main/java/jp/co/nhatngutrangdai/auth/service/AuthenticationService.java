package jp.co.nhatngutrangdai.auth.service;

import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import jp.co.nhatngutrangdai.auth.dto.LoginRequest;
import jp.co.nhatngutrangdai.auth.dto.LoginResponse;
import jp.co.nhatngutrangdai.auth.dto.UserResponse;
import jp.co.nhatngutrangdai.auth.entity.RoleCode;
import jp.co.nhatngutrangdai.auth.entity.UserAccount;
import jp.co.nhatngutrangdai.auth.entity.UserStatus;
import jp.co.nhatngutrangdai.auth.repository.UserAccountRepository;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthenticationService {

    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenService jwtTokenService;
    private final String dummyPasswordHash;

    public AuthenticationService(
            UserAccountRepository userAccountRepository,
            PasswordEncoder passwordEncoder,
            JwtTokenService jwtTokenService) {
        this.userAccountRepository = userAccountRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenService = jwtTokenService;
        this.dummyPasswordHash = passwordEncoder.encode(UUID.randomUUID().toString());
    }

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        String password = request.password();
        boolean bcryptCompatible = isBcryptCompatible(password);
        UserAccount user = userAccountRepository.findByEmailIgnoreCase(request.email().trim())
                .orElse(null);
        if (user == null || !bcryptCompatible) {
            passwordEncoder.matches("invalid-password-placeholder", dummyPasswordHash);
            throw invalidCredentials();
        }

        boolean passwordMatches = passwordEncoder.matches(password, user.getPasswordHash());
        if (!passwordMatches || user.getStatus() != UserStatus.ACTIVE) {
            throw invalidCredentials();
        }

        Set<RoleCode> roles = user.getRoles().stream()
                .map(role -> role.getCode())
                .collect(Collectors.toUnmodifiableSet());
        IssuedAccessToken token = jwtTokenService.issue(user.getId());

        return new LoginResponse(
                token.value(),
                "Bearer",
                token.expiresAt(),
                new UserResponse(user.getId(), user.getEmail(), roles.stream().sorted().toList()));
    }

    private boolean isBcryptCompatible(String password) {
        return password.getBytes(StandardCharsets.UTF_8).length <= 72;
    }

    private BadCredentialsException invalidCredentials() {
        return new BadCredentialsException("Email or password is incorrect");
    }
}
