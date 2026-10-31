# Backend Requirements

## 1. Product
REST backend cho hệ thống quản lý trường Nhật ngữ. Frontend và backend tách biệt.
- Java + Spring Boot; PostgreSQL.
- REST API prefix `/api/v1`.
- Layered Architecture, JWT authentication, role-based authorization.
- Hỗ trợ nghiệp vụ hiển thị tiếng Việt và tiếng Nhật; API dùng enum/code ổn định, frontend hiển thị bản dịch.

## 2. Roles
### STUDENT
Xem/cập nhật hồ sơ trong phạm vi cho phép; gửi đơn nhập học; xem trạng thái ghi danh, khóa/lớp, học phí, hóa đơn, thanh toán, lịch học, chuyên cần, kỳ thi, kết quả, tài liệu và thông báo của chính mình.

### ADMIN
Quản lý học viên, khóa học, lớp, dữ liệu giảng viên; xử lý đơn đăng ký; phân lớp; quản lý hóa đơn và bản ghi thanh toán; lịch học, điểm danh, kỳ thi, kết quả, tài liệu và thông báo. Giáo viên chỉ là dữ liệu nghiệp vụ trong MVP, chưa có tài khoản đăng nhập.

### SUPER_ADMIN
Quản lý tài khoản Admin và cấu hình/quyền quản trị đặc biệt. Hành động nhạy cảm vẫn phải được kiểm tra quyền và ghi audit.

## 3. MVP modules
1. Authentication and roles.
2. Student profiles.
3. Courses and classrooms.
4. Enrollment/application workflow.
5. Invoices and payment records.
6. Class schedules.
7. Attendance.
8. Exams and results.
9. Learning materials/documents.
10. Notifications.
11. Student/Admin dashboards.
12. Super Admin account management.

## 4. Public enrollment
Khách chưa đăng nhập có thể gửi đơn đăng ký. Đơn có reference khó đoán, trạng thái ban đầu và cơ chế tra cứu an toàn. Không cung cấp endpoint public để liệt kê hồ sơ hoặc làm lộ dữ liệu cá nhân. Quy trình tạo tài khoản/hồ sơ Student sau khi duyệt phải chống tạo trùng.

## 5. Enrollment statuses
`PENDING`, `APPROVED`, `CONFIRMED`, `COMPLETED`, `REJECTED`, `WAITLISTED`, `CANCELLED`.
Không cho chuyển trạng thái tùy ý. Lưu actor, thời điểm và ghi chú cho mỗi lần đổi trạng thái. Quy tắc chuyển trạng thái phải được xác nhận, không tự suy diễn.

## 6. Attendance statuses
`PRESENT`, `ABSENT`, `LATE`, `EXCUSED`.
Mỗi học viên chỉ có một bản ghi cho một buổi học. Chỉnh sửa sau khi chốt cần kiểm soát quyền và audit.

## 7. Finance
Phân biệt `Invoice`, `Payment`, `Transaction` và `Refund`; không gộp thành một entity/status. MVP có thể ghi nhận thanh toán thủ công nếu được xác nhận. Không tích hợp cổng thanh toán thật nếu chưa có provider và yêu cầu cụ thể. Thay đổi tài chính phải có audit trail.

## 8. Non-functional
- Validation ở API boundary và business rules ở service.
- Pagination/filter/sort cho list endpoints.
- Error response thống nhất.
- Transactions cho nghiệp vụ nhiều bước.
- Migration versioned; không dùng schema auto-create ở production.
- Không log password/token hoặc dữ liệu cá nhân không cần thiết.
- Unit + integration/API tests.
- Cấu hình qua environment variables, không commit secrets.
- CORS allowlist.
- Timestamp chuẩn UTC; hiển thị theo `Asia/Tokyo` khi cần.

## 9. Out of scope MVP
CRM nâng cao, CMS đầy đủ, ticketing, analytics nâng cao, learning portfolio, public reviews, teacher portal/login, online exam engine, multi-branch, payment gateway thật.

## 10. Open questions — must not invent
Ghi vào `docs/OPEN_QUESTIONS.md`: phiên bản Java/Spring; trường hồ sơ bắt buộc; quy tắc một học viên có nhiều enrollment/lớp; trạng thái và quy trình enrollment; học phí/giảm giá/công nợ/refund; phương thức thanh toán; quy tắc sĩ số và phân lớp; nghỉ/bù/đổi lịch; khóa điểm danh; thang điểm; file storage và giới hạn file; kênh thông báo; quy trình tạo tài khoản Student.
