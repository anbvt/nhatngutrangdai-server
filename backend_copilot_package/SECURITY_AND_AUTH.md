# Security and Authentication

## Core rules
- Backend là nơi quyết định quyền; frontend role check chỉ là UX.
- Default deny; chỉ public endpoint có chủ đích.
- Giới hạn mọi truy vấn theo ownership của student/user.
- Không tin `userId`, `studentId`, `role`, `isAdmin` từ client.
- Không cho học viên A xem dữ liệu học viên B dù biết ID.

## Password and tokens
- Hash password bằng Argon2id hoặc BCrypt phù hợp phiên bản Spring Security.
- Không lưu password plain text hoặc reversible.
- JWT access token ngắn hạn; key lấy từ environment/secret manager.
- Nếu có refresh token: rotate/revoke, lưu hash và xử lý reuse.
- Không commit `.env`, private key, token hoặc credential.

## Authorization
- STUDENT: dữ liệu của chính mình và lớp/tài liệu được cấp.
- ADMIN: quản lý nghiệp vụ theo quyền.
- SUPER_ADMIN: quản lý Admin và cấu hình đặc quyền.
- Kiểm tra role và ownership trong service/query khi cần; không chỉ dựa vào ẩn nút frontend.

## Audit sensitive actions
Ghi audit khi thay role, tạo/khóa Admin, duyệt enrollment, ghi/điều chỉnh payment/refund, sửa attendance đã chốt, sửa điểm và thao tác nhạy cảm khác.

## Input and file safety
Validate input; chống mass assignment bằng DTO; giới hạn kích thước/loại file; không tin filename/MIME client; chống path traversal; lưu file ngoài public webroot; download phải authorization hoặc signed URL ngắn hạn.

## Web security
CORS allowlist, không wildcard với credentials; CSRF strategy phù hợp token/cookie; rate limit login và public application/lookup; chống brute force và account enumeration; HTTPS production; security headers.

## Privacy
Chỉ thu thập dữ liệu cần thiết. Dùng synthetic data. Không log request body chứa PII hoặc credential. Xác định retention/archive theo quy định được xác nhận.
