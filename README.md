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
- Admin/Super Admin CRUD: `GET/POST /api/v1/students`, `GET/PATCH /api/v1/students/{id}`, `GET/POST /api/v1/courses`, `GET/PATCH /api/v1/courses/{id}`, `GET/POST /api/v1/classrooms`, `GET/PATCH /api/v1/classrooms/{id}`, `GET/POST /api/v1/teachers`, and `GET/PATCH /api/v1/teachers/{id}`.
- Students can access only their linked profile through `GET/PATCH /api/v1/students/me`; self-service edits are limited to phone and address.
- Public applicants can submit `POST /api/v1/public/enrollment-applications` with required `fullName`, `email`, `phone`, and `courseId`; optional fields are `dateOfBirth`, `address`, and `nationality`. The response contains a one-time random reference and `PENDING` status, but no personal details.
- Admin/Super Admin can list/filter `GET /api/v1/enrollments?status=&courseId=` and view `GET /api/v1/enrollments/{id}` including status history. Pending applications are deduplicated by normalized email and course. Public reference lookup, status transitions, class assignment, and conversion/linking to a Student account are not implemented pending business-rule decisions.
- Admin/Super Admin finance APIs: `POST /api/v1/invoices` (issue invoice with items), `GET /api/v1/invoices`, `GET /api/v1/invoices/{id}`, `POST /api/v1/payments`, `GET /api/v1/payments`, and read-only `GET /api/v1/transactions`. Student self-service: `GET /api/v1/students/me/invoices`, `GET /api/v1/students/me/invoices/{id}`, and `GET /api/v1/students/me/payments`.

List endpoints accept zero-based `page`, `size` (default 20, maximum 100), `sort` (resource-specific allowlist), optional `q`, and optional `status`. Classroom lists also accept `courseId`. POST creates return `201`; validation failures return the standard `400` response; duplicate student codes and account links return `409`; missing records return `404`.

Student codes are required and unique. Student email is optional and non-unique. Student account links are nullable and one-to-one; only active accounts with the `STUDENT` role can be linked, by an admin. Course duration is an optional display label. Course tuition amount/currency are optional and have no inferred currency default. Status values for core records are `ACTIVE` and `INACTIVE`. Classroom capacity must be positive; dates are optional, but `endDate` cannot precede `startDate`.

PATCH semantics: omitted properties are unchanged; explicit `null` clears optional fields. Required fields reject `null` or blank values. A Student may update only their linked profile's phone/address; user ID and other fields cannot be supplied through that endpoint.

Invoices are issued immediately with generated invoice numbers, required three-letter currency and line items; tax/discount are not applied. Manual payments may be partial but cannot exceed invoice balance. Every payment creates a separate immutable ledger transaction and finance audit event. Refunds, invoice editing/voiding, overdue status, and payment gateway integration are not implemented.

Refresh tokens, logout, public Student registration, password reset, and account-management APIs are not implemented. Schedule, attendance, exams, materials, notifications, and dashboards remain for later phases.
