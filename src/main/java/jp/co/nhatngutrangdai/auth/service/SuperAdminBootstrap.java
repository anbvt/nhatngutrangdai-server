package jp.co.nhatngutrangdai.auth.service;

import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.util.Set;

import jp.co.nhatngutrangdai.auth.entity.RoleCode;
import jp.co.nhatngutrangdai.auth.entity.UserAccount;
import jp.co.nhatngutrangdai.auth.dto.LoginRequest;
import jp.co.nhatngutrangdai.auth.repository.RoleRepository;
import jp.co.nhatngutrangdai.auth.repository.UserAccountRepository;
import jakarta.validation.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class SuperAdminBootstrap implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(SuperAdminBootstrap.class);

    private final String email;
    private final String password;
    private final UserAccountRepository userAccountRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final Clock clock;
    private final Validator validator;

    public SuperAdminBootstrap(
            @Value("${app.security.bootstrap-super-admin.email:}") String email,
            @Value("${app.security.bootstrap-super-admin.password:}") String password,
            UserAccountRepository userAccountRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder,
            Clock clock,
            Validator validator) {
        this.email = email.trim();
        this.password = password;
        this.userAccountRepository = userAccountRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.clock = clock;
        this.validator = validator;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (email.isBlank() && password.isBlank()) {
            log.warn("No bootstrap Super Admin configured; no initial administrator was created");
            return;
        }
        if (email.isBlank() || password.isBlank()) {
            throw new IllegalStateException(
                    "BOOTSTRAP_SUPER_ADMIN_EMAIL and BOOTSTRAP_SUPER_ADMIN_PASSWORD must be configured together");
        }
        if (!validator.validateValue(LoginRequest.class, "email", email).isEmpty()) {
            throw new IllegalStateException("BOOTSTRAP_SUPER_ADMIN_EMAIL must be a valid email address");
        }
        if (password.getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new IllegalStateException("Bootstrap password exceeds the BCrypt 72-byte limit");
        }

        var existingUser = userAccountRepository.findByEmailIgnoreCase(email);
        if (existingUser.isPresent()) {
            boolean alreadySuperAdmin = existingUser.get().getRoles().stream()
                    .anyMatch(role -> role.getCode() == RoleCode.SUPER_ADMIN);
            if (!alreadySuperAdmin) {
                throw new IllegalStateException(
                        "Bootstrap email already belongs to an account without the SUPER_ADMIN role");
            }
            log.info("Bootstrap Super Admin already exists; no account changes were made");
            return;
        }

        var superAdminRole = roleRepository.findById(RoleCode.SUPER_ADMIN)
                .orElseThrow(() -> new IllegalStateException("SUPER_ADMIN role is missing from the database"));
        UserAccount account = UserAccount.active(
                email,
                passwordEncoder.encode(password),
                Set.of(superAdminRole),
                clock.instant());
        userAccountRepository.save(account);
        log.info("Bootstrap Super Admin account created");
    }
}
