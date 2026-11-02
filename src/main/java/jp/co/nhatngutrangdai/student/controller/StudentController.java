package jp.co.nhatngutrangdai.student.controller;

import java.util.UUID;

import jp.co.nhatngutrangdai.auth.service.AuthenticatedUser;
import jp.co.nhatngutrangdai.common.api.PageResponse;
import jp.co.nhatngutrangdai.common.entity.SchoolStatus;
import jp.co.nhatngutrangdai.student.dto.StudentCreateRequest;
import jp.co.nhatngutrangdai.student.dto.StudentPatchRequest;
import jp.co.nhatngutrangdai.student.dto.StudentProfilePatchRequest;
import jp.co.nhatngutrangdai.student.dto.StudentProfileResponse;
import jp.co.nhatngutrangdai.student.dto.StudentResponse;
import jp.co.nhatngutrangdai.student.service.StudentService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/students")
@SecurityRequirement(name = "bearerAuth")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public PageResponse<StudentResponse> list(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) SchoolStatus status,
            @PageableDefault(size = 20) Pageable pageable) {
        return PageResponse.from(studentService.list(q, status, pageable));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public StudentResponse create(@Valid @RequestBody StudentCreateRequest request) {
        return studentService.create(request);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public StudentResponse get(@PathVariable UUID id) {
        return studentService.get(id);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public StudentResponse update(
            @PathVariable UUID id,
            @Valid @RequestBody StudentPatchRequest request) {
        return studentService.update(id, request);
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('STUDENT')")
    public StudentProfileResponse getMyProfile(@AuthenticationPrincipal AuthenticatedUser user) {
        return studentService.getMyProfile(user);
    }

    @PatchMapping("/me")
    @PreAuthorize("hasRole('STUDENT')")
    public StudentProfileResponse updateMyProfile(
            @AuthenticationPrincipal AuthenticatedUser user,
            @Valid @RequestBody StudentProfilePatchRequest request) {
        return studentService.updateMyProfile(user, request);
    }
}
