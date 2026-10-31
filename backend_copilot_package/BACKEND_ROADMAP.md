# Backend Roadmap

Thực hiện từng phase; sau mỗi phase chạy tests/build và báo cáo thay đổi, giả định, kết quả. Không sinh toàn bộ backend trong một lần.

## Phase 0 — Assessment
Kiểm tra Git status, build tool, Java/Spring, cấu trúc package, DB/migrations, security, tests, Docker/CI. Tạo `docs/IMPLEMENTATION_PLAN.md`, `docs/OPEN_QUESTIONS.md`, `docs/ARCHITECTURE_DECISIONS.md`. Chưa sửa lớn.
**Done:** báo cáo hiện trạng, code tái sử dụng, rủi ro, cấu trúc đề xuất, file dự kiến thay đổi.

## Phase 1 — Foundation
Config dev/test/prod; PostgreSQL env config; migrations; global exception handling; validation; response/pagination; health check; OpenAPI nếu phù hợp; tests.
**Done:** app chạy, migration chạy trên DB trống, build/tests pass.

## Phase 2 — Auth and authorization
User/Role, password hash, login, `/auth/me`, JWT, endpoint/method authorization, student ownership tests. Refresh/logout theo flow đã chốt.
**Done:** test 401/403 và truy cập chéo dữ liệu.

## Phase 3 — Core school data
Students, courses, classrooms, teachers; CRUD, validation, pagination, filtering, authorization.
## Phase 4 — Enrollment
Public application, safe reference lookup, admin review/status transition, assign class, status history/audit, duplicate prevention.
## Phase 5 — Finance
Invoices/items, manual payment nếu được xác nhận, allocation/refund theo quy tắc đã duyệt, transaction và audit. Không payment gateway thật.
## Phase 6 — Schedule and attendance
Class sessions, attendance, unique constraints, controlled bulk update, audit chỉnh sửa sau chốt.
## Phase 7 — Exams and results
Exam, results, score validation, access rules and audit.
## Phase 8 — Materials and notifications
Metadata/storage abstraction, authorized upload/download, notification/read status. Email/SMS để phase sau nếu chưa cấu hình.
## Phase 9 — Dashboard and integration
Role-specific dashboard, frontend contract check, CORS, indexes/performance, integration tests, Docker/production config, README.

## Completion checklist
- [ ] Build và tests pass.
- [ ] Migrations chạy trên DB trống.
- [ ] Không có secret trong Git.
- [ ] Auth/ownership tests pass.
- [ ] List APIs có pagination.
- [ ] API docs cập nhật.
- [ ] README setup rõ ràng.
- [ ] Open questions/assumptions được ghi lại.
- [ ] Không có thao tác Git phá hủy.
