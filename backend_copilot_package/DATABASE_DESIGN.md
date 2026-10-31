# Proposed Database Design

Đây là mô hình đề xuất ban đầu, chưa phải schema nghiệp vụ đã được chốt. Copilot cần rà soát codebase và ghi câu hỏi mở trước khi tạo migration.

## Conventions
PostgreSQL; UUID được khuyến nghị cho ID public nếu phù hợp codebase; `created_at`, `updated_at`; audit fields cho dữ liệu quan trọng; timestamp UTC; enum code ổn định; không hard-delete lịch sử tài chính/enrollment/attendance đã chốt.

## Identity
- `users`: id, email/username, password_hash, status, last_login_at, created_at, updated_at.
- `roles`: id, code (`STUDENT`, `ADMIN`, `SUPER_ADMIN`), name.
- `user_roles`: user_id, role_id, unique pair.
- `refresh_tokens` (nếu có refresh flow): user_id, token_hash, expires_at, revoked_at.

## Student and school
- `students`: id, nullable user_id cho guest flow, student_code, full_name, date_of_birth, email, phone, address, nationality, status, timestamps.
- `courses`: id, code, name, description, level, duration, tuition_amount, currency, status.
- `classrooms`: id, course_id, code, name, capacity, start_date, end_date, status.
- `teachers`: id, full_name, email, phone, status; không cần login trong MVP.
- `class_teacher_assignments`: classroom_id, teacher_id, assigned_from, assigned_to.

## Enrollment
- `enrollments`: id, student_id nullable nếu guest, course_id, classroom_id nullable, application_reference unique, status, submitted_at, reviewed_at, reviewed_by, review_note, timestamps.
- `enrollment_status_history`: enrollment_id, old_status, new_status, actor_id, occurred_at, note.
Cần xác nhận form fields và quy trình chuyển guest application thành student/account.

## Finance
- `invoices`: id, student_id, enrollment_id nullable, invoice_number, issue_date, due_date, total_amount, currency, status.
- `invoice_items`: invoice_id, description, quantity, unit_amount, line_total.
- `payments`: id, student_id, invoice_id nullable theo allocation rule, amount, currency, paid_at, method, reference, recorded_by, note, status.
- `payment_allocations`: payment_id, invoice_id, allocated_amount (nếu một payment có thể phân bổ nhiều invoice).
- `refunds`: payment_id, amount, reason, status, requested_at, processed_at, processed_by.

## Schedule and academic
- `class_sessions`: classroom_id, starts_at, ends_at, location, status.
- `attendance_records`: class_session_id, student_id, status, note, recorded_by, recorded_at; unique `(class_session_id, student_id)`.
- `exams`: classroom_id, title, description, exam_date, max_score, status.
- `exam_results`: exam_id, student_id, score, grade, feedback, recorded_by; unique `(exam_id, student_id)`.

## Materials and notifications
- `materials`: title, description, storage_key, mime_type, file_size, uploaded_by, status, created_at.
- `material_class_assignments`: material_id, classroom_id.
- `notifications`: title, body, category, created_by, scheduled_at, status.
- `user_notifications`: notification_id, user_id, read_at, delivered_at.

## Audit
- `audit_logs`: actor_user_id, action, resource_type, resource_id, occurred_at, metadata JSONB có kiểm soát.
Không ghi password, token hoặc toàn bộ dữ liệu cá nhân trong metadata.

## Decisions to confirm
Quan hệ user-student; uniqueness email/student_code; nhiều enrollment/lớp; thay course có tạo enrollment mới không; nhiều teacher/lớp; một invoice nhiều payments; một payment nhiều invoices; quy tắc refund; lưu file ở đâu; retention dữ liệu.
