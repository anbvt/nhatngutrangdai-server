package jp.co.nhatngutrangdai.teacher.service;

import java.util.Set;
import java.util.UUID;

import jp.co.nhatngutrangdai.common.entity.SchoolStatus;
import jp.co.nhatngutrangdai.common.exception.ApiExceptionFactory;
import jp.co.nhatngutrangdai.common.persistence.PageableSupport;
import jp.co.nhatngutrangdai.teacher.dto.TeacherCreateRequest;
import jp.co.nhatngutrangdai.teacher.dto.TeacherPatchRequest;
import jp.co.nhatngutrangdai.teacher.dto.TeacherResponse;
import jp.co.nhatngutrangdai.teacher.entity.Teacher;
import jp.co.nhatngutrangdai.teacher.mapper.TeacherMapper;
import jp.co.nhatngutrangdai.teacher.repository.TeacherRepository;
import jp.co.nhatngutrangdai.teacher.repository.TeacherSpecifications;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TeacherService {

    private static final Set<String> SORT_FIELDS = Set.of(
            "fullName", "email", "status", "createdAt", "updatedAt");

    private final TeacherRepository teacherRepository;
    private final TeacherMapper teacherMapper;

    public TeacherService(TeacherRepository teacherRepository, TeacherMapper teacherMapper) {
        this.teacherRepository = teacherRepository;
        this.teacherMapper = teacherMapper;
    }

    @Transactional(readOnly = true)
    public Page<TeacherResponse> list(String query, SchoolStatus status, Pageable pageable) {
        validateQuery(query);
        Pageable safePageable = PageableSupport.validated(pageable, SORT_FIELDS, "fullName");
        return teacherRepository.findAll(TeacherSpecifications.filter(query, status), safePageable)
                .map(teacherMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public TeacherResponse get(UUID id) {
        return teacherMapper.toResponse(findTeacher(id));
    }

    @Transactional
    public TeacherResponse create(TeacherCreateRequest request) {
        Teacher teacher = Teacher.create(
                request.fullName(),
                request.email(),
                request.phone(),
                request.status());
        return teacherMapper.toResponse(teacherRepository.save(teacher));
    }

    @Transactional
    public TeacherResponse update(UUID id, TeacherPatchRequest request) {
        Teacher teacher = findTeacher(id);
        if (request.fullNamePresent()) {
            teacher.setFullName(requireNonBlank(request.fullName(), "fullName"));
        }
        if (request.emailPresent()) {
            teacher.setEmail(request.email());
        }
        if (request.phonePresent()) {
            teacher.setPhone(request.phone());
        }
        if (request.statusPresent()) {
            teacher.setStatus(requirePresent(request.status(), "status"));
        }
        teacherRepository.flush();
        return teacherMapper.toResponse(teacher);
    }

    private Teacher findTeacher(UUID id) {
        return teacherRepository.findById(id)
                .orElseThrow(() -> ApiExceptionFactory.notFound("Teacher"));
    }

    private String requireNonBlank(String value, String field) {
        if (value == null || value.isBlank()) {
            throw ApiExceptionFactory.badRequest(field + " cannot be null or blank");
        }
        return value;
    }

    private <T> T requirePresent(T value, String field) {
        if (value == null) {
            throw ApiExceptionFactory.badRequest(field + " cannot be null");
        }
        return value;
    }

    private void validateQuery(String query) {
        if (query != null && query.length() > 120) {
            throw ApiExceptionFactory.badRequest("Search query must be at most 120 characters");
        }
    }
}
