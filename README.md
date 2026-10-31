# Japanese Language School Management Backend

## Requirements

- Java 21
- PostgreSQL for local and production runs
- Maven Wrapper is included; a separate Maven installation is not required

## Configuration

Database credentials are read from environment variables. Do not commit real credentials.

```powershell
$env:DB_URL = "jdbc:postgresql://localhost:5432/nhat_ngu_trang_dai"
$env:DB_USERNAME = "your_local_db_user"
$env:DB_PASSWORD = "your_local_db_password"

$jwtBytes = New-Object byte[] 32
$rng = [Security.Cryptography.RandomNumberGenerator]::Create()
$rng.GetBytes($jwtBytes)
$rng.Dispose()
$env:JWT_SECRET = [Convert]::ToBase64String($jwtBytes)

$env:BOOTSTRAP_SUPER_ADMIN_EMAIL = "initial-admin@example.com"
$env:BOOTSTRAP_SUPER_ADMIN_PASSWORD = "set-a-unique-secret-password"
```

`JWT_SECRET` must be Base64 that decodes to at least 32 cryptographically random bytes. Never commit or log it. The default profile is `dev`; set `SPRING_PROFILES_ACTIVE=prod` for production. The `test` profile uses an in-memory H2 database and is intended only for automated tests.

On the first deployment, provide both `BOOTSTRAP_SUPER_ADMIN_EMAIL` and `BOOTSTRAP_SUPER_ADMIN_PASSWORD` to create the initial Super Admin. The password is BCrypt-hashed before storage. Remove those variables after bootstrap; subsequent startup with the same email does not change the account or password. If that email already belongs to an account without the `SUPER_ADMIN` role, startup fails instead of escalating that account.

After the first successful startup, remove the bootstrap variables from the current PowerShell session with `Remove-Item Env:BOOTSTRAP_SUPER_ADMIN_EMAIL, Env:BOOTSTRAP_SUPER_ADMIN_PASSWORD`.

## Run and verify

```powershell
.\mvnw.cmd test
.\mvnw.cmd spring-boot:run
```

Flyway applies versioned migrations on startup. Hibernate schema generation is set to `validate`; it does not create or alter production tables. The baseline migration is schema-neutral; migration V2 adds identity and role tables only.

## Endpoints

- `GET /actuator/health` — health status only; detailed component data is not exposed.
- `/swagger-ui.html` — interactive OpenAPI UI.
- `/api-docs` — generated OpenAPI document.
- `POST /api/v1/auth/login` — authenticate with email and password; returns a 15-minute JWT access token.
- `GET /api/v1/auth/me` — return the authenticated account and roles; requires `Authorization: Bearer <access-token>`.

Refresh tokens, logout, public Student registration, password reset, and account-management APIs are not implemented. No domain endpoints are available yet. Future list endpoints should use zero-based `page`, default `size` 20, maximum `size` 100, and endpoint-specific sort allowlists.
