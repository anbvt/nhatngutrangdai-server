package jp.co.nhatngutrangdai.student.service;

import java.util.Set;
import java.util.UUID;

import jp.co.nhatngutrangdai.auth.entity.RoleCode;
import jp.co.nhatngutrangdai.auth.entity.UserAccount;
import jp.co.nhatngutrangdai.auth.entity.UserStatus;
import jp.co.nhatngutrangdai.auth.repository.UserAccountRepository;
import jp.co.nhatngutrangdai.auth.service.AuthenticatedUser;
import jp.co.nhatngutrangdai.common.entity.SchoolStatus;
import jp.co.nhatngutrangdai.common.exception.ApiExceptionFactory;
import jp.co.nhatngutrangdai.common.persistence.PageableSupport;
import jp.co.nhatngutrangdai.student.dto.StudentCreateRequest;
import jp.co.nhatngutrangdai.student.dto.StudentPatchRequest;
import jp.co.nhatngutrangdai.student.dto.StudentProfilePatchRequest;
import jp.co.nhatngutrangdai.student.dto.StudentProfileResponse;
import jp.co.nhatngutrangdai.student.dto.StudentResponse;
import jp.co.nhatngutrangdai.student.entity.Student;
import jp.co.nhatngutrangdai.student.mapper.StudentMapper;
import jp.co.nhatngutrangdai.student.repository.StudentRepository;
import jp.co.nhatngutrangdai.student.repository.StudentSpecifications;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StudentService {

    private static final Set<String> SORT_FIELDS = Set.of(
            "studentCode", "fullName", "email", "status", "createdAt", "updatedAt");

    private final StudentRepository studentRepository;
    private final UserAccountRepository userAccountRepository;
    private final StudentMapper studentMapper;

    public StudentService(
            StudentRepository studentRepository,
            UserAccountRepository userAccountRepository,
            StudentMapper studentMapper) {
        this.studentRepository = studentRepository;
        this.userAccountRepository = userAccountRepository;
        this.studentMapper = studentMapper;
    }

    @Transactional(readOnly = true)
    public Page<StudentResponse> list(String query, SchoolStatus status, Pageable pageable) {
        validateQuery(query);
        Pageable safePageable = PageableSupport.validated(pageable, SORT_FIELDS, "studentCode");
        return studentRepository.findAll(StudentSpecifications.filter(query, status), safePageable)
                .map(studentMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public StudentResponse get(UUID id) {
        return studentMapper.toResponse(findStudent(id));
    }

    @Transactional
    public StudentResponse create(StudentCreateRequest request) {
        String studentCode = request.studentCode().trim();
        if (studentRepository.existsByStudentCode(studentCode)) {
            throw ApiExceptionFactory.conflict("Student code is already in use");
        }
        UserAccount userAccount = request.userId() == null ? null : findStudentUser(request.userId());
        if (userAccount != null && studentRepository.existsByUserAccount_Id(userAccount.getId())) {
            throw ApiExceptionFactory.conflict("User account is already linked to a student");
        }

        Student student = Student.create(
                studentCode,
                request.fullName(),
                request.dateOfBirth(),
                request.email(),
                request.phone(),
                request.address(),
                request.nationality(),
                request.status(),
                userAccount);
        return studentMapper.toResponse(studentRepository.save(student));
    }

    @Transactional
    public StudentResponse update(UUID id, StudentPatchRequest request) {
        Student student = findStudent(id);
        if (request.studentCodePresent()) {
            String value = requireNonBlank(request.studentCode(), "studentCode");
            if (studentRepository.existsByStudentCodeAndIdNot(value.trim(), id)) {
                throw ApiExceptionFactory.conflict("Student code is already in use");
            }
            student.setStudentCode(value);
        }
        if (request.fullNamePresent()) {
            student.setFullName(requireNonBlank(request.fullName(), "fullName"));
        }
        if (request.dateOfBirthPresent()) {
            student.setDateOfBirth(request.dateOfBirth());
        }
        if (request.emailPresent()) {
            student.setEmail(request.email());
        }
        if (request.phonePresent()) {
            student.setPhone(request.phone());
        }
        if (request.addressPresent()) {
            student.setAddress(request.address());
        }
        if (request.nationalityPresent()) {
            student.setNationality(request.nationality());
        }
        if (request.statusPresent()) {
            student.setStatus(requirePresent(request.status(), "status"));
        }
        if (request.userIdPresent()) {
            UserAccount userAccount = request.userId() == null ? null : findStudentUser(request.userId());
            if (userAccount != null && studentRepository.existsByUserAccount_IdAndIdNot(userAccount.getId(), id)) {
                throw ApiExceptionFactory.conflict("User account is already linked to a student");
            }
            student.setUserAccount(userAccount);
        }
        studentRepository.flush();
        return studentMapper.toResponse(student);
    }

    @Transactional(readOnly = true)
    public StudentProfileResponse getMyProfile(AuthenticatedUser user) {
        return studentMapper.toProfileResponse(findByUserId(user.id()));
    }

    @Transactional
    public StudentProfileResponse updateMyProfile(AuthenticatedUser user, StudentProfilePatchRequest request) {
        if (!request.phonePresent() && !request.addressPresent()) {
            throw ApiExceptionFactory.badRequest("At least one editable profile field must be provided");
        }
        Student student = findByUserId(user.id());
        if (request.phonePresent()) {
            student.setPhone(request.phone());
        }
        if (request.addressPresent()) {
            student.setAddress(request.address());
        }
        studentRepository.flush();
        return studentMapper.toProfileResponse(student);
    }

    private Student findStudent(UUID id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> ApiExceptionFactory.notFound("Student"));
    }

    private Student findByUserId(UUID userId) {
        return studentRepository.findByUserAccount_Id(userId)
                .orElseThrow(() -> ApiExceptionFactory.notFound("Student profile"));
    }

    private UserAccount findStudentUser(UUID userId) {
        UserAccount user = userAccountRepository.findById(userId)
                .orElseThrow(() -> ApiExceptionFactory.notFound("User account"));
        boolean hasStudentRole = user.getRoles().stream().anyMatch(role -> role.getCode() == RoleCode.STUDENT);
        if (user.getStatus() != UserStatus.ACTIVE || !hasStudentRole) {
            throw ApiExceptionFactory.conflict("Only active Student accounts can be linked to a student profile");
        }
        return user;
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
