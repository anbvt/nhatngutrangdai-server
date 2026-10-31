# Proposed REST API Contract

Các endpoint dưới đây là surface dự kiến, cần đối chiếu API hiện có trước khi implement. Không tự đặt business rules chưa xác nhận.

## Conventions
Base `/api/v1`; JSON camelCase; Bearer access token; list pagination/filter/sort; request/response DTO riêng; không trả entity.

## Auth
- `POST /auth/login`
- `POST /auth/refresh` (nếu refresh flow được duyệt)
- `POST /auth/logout`
- `GET /auth/me`
- Forgot/reset password chỉ làm khi có email flow rõ ràng.

## Public enrollment
- `POST /public/enrollment-applications`
- `GET /public/enrollment-applications/{reference}` chỉ khi có xác minh bổ sung an toàn.
Không có public list endpoint và không làm lộ PII.

## Student portal
- `GET/PATCH /students/me`
- `GET /students/me/enrollments`
- `GET /students/me/invoices`
- `GET /students/me/payments`
- `GET /students/me/schedule`
- `GET /students/me/attendance`
- `GET /students/me/exams`
- `GET /students/me/materials`
- `GET /students/me/notifications`
- `PATCH /students/me/notifications/{id}`

## Admin
- Students: `GET/POST /students`, `GET/PATCH /students/{id}`
- Courses: `GET/POST /courses`, `GET/PATCH /courses/{id}`
- Classrooms: `GET/POST /classrooms`, `GET/PATCH /classrooms/{id}`
- Teachers: `GET/POST /teachers`, `PATCH /teachers/{id}`
- Enrollment: `GET /enrollments`, `GET /enrollments/{id}`, `PATCH /enrollments/{id}/status`, `POST /enrollments/{id}/assign-class`
- Finance: `GET/POST /invoices`, `GET /invoices/{id}`, `GET /payments`, `POST /payments`; refund endpoint chỉ khi nghiệp vụ được xác nhận.
- Schedule: `GET/POST /class-sessions`, `GET/PATCH /class-sessions/{id}`
- Attendance: `GET /class-sessions/{id}/attendance`; bulk update chỉ nếu quy tắc được chốt.
- Exams: `GET/POST /exams`, `GET/PATCH /exams/{id}`, `GET /exams/{id}/results`; bulk result update nếu được duyệt.
- Materials: `GET/POST /materials`, `GET/PATCH /materials/{id}`, `POST /materials/{id}/assignments`
- Notifications: `GET/POST /notifications`, `GET /notifications/{id}`

## Super Admin and dashboard
- Super Admin: `GET/POST /admin-accounts`, `PATCH /admin-accounts/{id}`, `PATCH /admin-accounts/{id}/status`.
- Dashboard: `GET /dashboard/student`, `/dashboard/admin`, `/dashboard/super-admin`.

## Status codes
`400` validation; `401` unauthenticated; `403` forbidden; `404` not found/not disclosable; `409` conflict; `422` business validation if adopted consistently; `500` generic server error.

## Contract-first
Với mỗi module: xác định use case và quyền → DTO → validation/error codes → pagination/filter → API tests → persistence/service/controller. Duy trì OpenAPI hoặc tài liệu tương đương. Ưu tiên tương thích với API client hiện có.
