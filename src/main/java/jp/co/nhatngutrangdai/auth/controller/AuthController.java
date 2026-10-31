package jp.co.nhatngutrangdai.auth.controller;

import java.util.List;

import jp.co.nhatngutrangdai.auth.dto.LoginRequest;
import jp.co.nhatngutrangdai.auth.dto.LoginResponse;
import jp.co.nhatngutrangdai.auth.dto.UserResponse;
import jp.co.nhatngutrangdai.auth.service.AuthenticatedUser;
import jp.co.nhatngutrangdai.auth.service.AuthenticationService;
import jp.co.nhatngutrangdai.auth.entity.RoleCode;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT")
public class AuthController {

    private final AuthenticationService authenticationService;

    public AuthController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/login")
    @Operation(summary = "Authenticate with email and password")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        return authenticationService.login(request);
    }

    @GetMapping("/me")
    @Operation(
            summary = "Return the authenticated user",
            security = @SecurityRequirement(name = "bearerAuth"))
    public UserResponse currentUser(@AuthenticationPrincipal AuthenticatedUser user) {
        List<RoleCode> roles = user.roles().stream().sorted().toList();
        return new UserResponse(user.id(), user.email(), roles);
    }
}
