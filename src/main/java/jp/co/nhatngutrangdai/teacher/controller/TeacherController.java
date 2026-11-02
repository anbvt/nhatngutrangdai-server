package jp.co.nhatngutrangdai.teacher.controller;

import java.util.UUID;

import jp.co.nhatngutrangdai.common.api.PageResponse;
import jp.co.nhatngutrangdai.common.entity.SchoolStatus;
import jp.co.nhatngutrangdai.teacher.dto.TeacherCreateRequest;
import jp.co.nhatngutrangdai.teacher.dto.TeacherPatchRequest;
import jp.co.nhatngutrangdai.teacher.dto.TeacherResponse;
import jp.co.nhatngutrangdai.teacher.service.TeacherService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
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
@RequestMapping("/api/v1/teachers")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
@SecurityRequirement(name = "bearerAuth")
public class TeacherController {

    private final TeacherService teacherService;

    public TeacherController(TeacherService teacherService) {
        this.teacherService = teacherService;
    }

    @GetMapping
    public PageResponse<TeacherResponse> list(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) SchoolStatus status,
            @PageableDefault(size = 20) Pageable pageable) {
        return PageResponse.from(teacherService.list(q, status, pageable));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TeacherResponse create(@Valid @RequestBody TeacherCreateRequest request) {
        return teacherService.create(request);
    }

    @GetMapping("/{id}")
    public TeacherResponse get(@PathVariable UUID id) {
        return teacherService.get(id);
    }

    @PatchMapping("/{id}")
    public TeacherResponse update(
            @PathVariable UUID id,
            @Valid @RequestBody TeacherPatchRequest request) {
        return teacherService.update(id, request);
    }
}
