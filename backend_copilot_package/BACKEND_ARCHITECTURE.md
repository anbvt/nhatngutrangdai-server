# Backend Architecture

## 1. Repository assessment first
Kiểm tra Git status, `pom.xml`/`build.gradle`, Java/Spring versions, package structure, database, migrations, security, tests, Docker và CI. Giữ build tool/framework hiện tại nếu đang hoạt động; không nâng major version hoặc xóa code khi chưa được duyệt.

## 2. Layered architecture
Tổ chức theo feature/module, ví dụ:
```text
config/
common/ (exception, response, validation, security, audit)
auth/
student/
course/
classroom/
enrollment/
finance/
schedule/
attendance/
exam/
material/
notification/
admin/
```
Trong mỗi module, dùng các layer phù hợp: `controller`, `service`, `repository`, `entity`, `dto`, `mapper`.

- Controller: HTTP mapping, nhận DTO, trả response; không chứa business logic.
- Service: use cases, business rules, transaction boundaries.
- Repository: persistence/query.
- Entity: persistence model, không trả trực tiếp ra API.
- DTO: tách request và response.
- Mapper: chuyển DTO/entity, tận dụng công cụ hiện có.

## 3. Conventions
Java class PascalCase; method/field camelCase; enum UPPER_SNAKE_CASE; JSON camelCase; REST resource là danh từ số nhiều; API prefix `/api/v1`. Không trả JPA entity trực tiếp.

## 4. Error response
Chuẩn hóa qua `@RestControllerAdvice` hoặc tương đương. Ví dụ:
```json
{
  "timestamp": "2026-01-01T00:00:00Z",
  "status": 400,
  "code": "VALIDATION_ERROR",
  "message": "Request validation failed",
  "fieldErrors": [{"field": "email", "message": "Invalid email"}],
  "path": "/api/v1/students"
}
```
Không trả stack trace, SQL hay exception nội bộ.

## 5. Pagination
Danh sách phải phân trang. Đề xuất `page` bắt đầu 0, `size` mặc định 20 và tối đa 100, `sort` theo allowlist. Nếu project có convention khác, giữ nhất quán và ghi rõ.

## 6. Validation and transactions
Dùng Bean Validation cho request DTO; business validation ở service; `@Transactional` cho use case cập nhật nhiều entity. Không nuốt exception. Cân nhắc optimistic locking cho cập nhật đồng thời.

## 7. Persistence
PostgreSQL và migration tool đã có; nếu chưa có, đề xuất Flyway/Liquibase trước khi thêm. Không dùng `ddl-auto=create`/`create-drop` production. Thêm FK, unique constraints và indexes có chủ đích; tránh N+1; không cascade-delete dữ liệu tài chính hoặc lịch sử.

## 8. Tests and observability
Unit tests cho service; API tests cho validation/auth; integration tests cho persistence trọng yếu. Test truy cập chéo dữ liệu học viên. Logging không chứa credential/token; audit thao tác nhạy cảm; health check phù hợp.
