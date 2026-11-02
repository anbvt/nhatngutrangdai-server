package jp.co.nhatngutrangdai.classroom.controller;

import java.util.UUID;

import jp.co.nhatngutrangdai.classroom.dto.ClassroomCreateRequest;
import jp.co.nhatngutrangdai.classroom.dto.ClassroomPatchRequest;
import jp.co.nhatngutrangdai.classroom.dto.ClassroomResponse;
import jp.co.nhatngutrangdai.classroom.service.ClassroomService;
import jp.co.nhatngutrangdai.common.api.PageResponse;
import jp.co.nhatngutrangdai.common.entity.SchoolStatus;
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
@RequestMapping("/api/v1/classrooms")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
@SecurityRequirement(name = "bearerAuth")
public class ClassroomController {

    private final ClassroomService classroomService;

    public ClassroomController(ClassroomService classroomService) {
        this.classroomService = classroomService;
    }

    @GetMapping
    public PageResponse<ClassroomResponse> list(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) SchoolStatus status,
            @RequestParam(required = false) UUID courseId,
            @PageableDefault(size = 20) Pageable pageable) {
        return PageResponse.from(classroomService.list(q, status, courseId, pageable));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ClassroomResponse create(@Valid @RequestBody ClassroomCreateRequest request) {
        return classroomService.create(request);
    }

    @GetMapping("/{id}")
    public ClassroomResponse get(@PathVariable UUID id) {
        return classroomService.get(id);
    }

    @PatchMapping("/{id}")
    public ClassroomResponse update(
            @PathVariable UUID id,
            @Valid @RequestBody ClassroomPatchRequest request) {
        return classroomService.update(id, request);
    }
}
