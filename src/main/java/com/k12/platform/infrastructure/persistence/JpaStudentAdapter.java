package com.k12.platform.infrastructure.persistence;

import com.k12.platform.domain.model.Student;
import com.k12.platform.domain.port.StudentRepository;
import com.k12.platform.domain.model.valueobjects.*;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

/**
 * JPA-based implementation of StudentRepository port.
 */
@ApplicationScoped
public class JpaStudentAdapter implements StudentRepository {

    @Override
    @Transactional
    public void save(Student student) {
        PanacheStudentEntity existing =
                PanacheStudentEntity.findById(student.studentId().value());

        if (existing == null) {
            // Insert new entity
            PanacheStudentEntity entity = toEntity(student);
            Instant now = Instant.now();
            entity.setCreatedAt(java.time.OffsetDateTime.ofInstant(now, ZoneOffset.UTC));
            entity.setUpdatedAt(java.time.OffsetDateTime.ofInstant(now, ZoneOffset.UTC));
            entity.persist();
        } else {
            // Update existing entity
            updateFromDomain(existing, student);
            existing.setUpdatedAt(java.time.OffsetDateTime.ofInstant(Instant.now(), ZoneOffset.UTC));
        }
    }

    private void updateFromDomain(PanacheStudentEntity entity, Student student) {
        entity.setFirstName(student.personalInfo().firstName());
        entity.setLastName(student.personalInfo().lastName());
        entity.setDateOfBirth(student.personalInfo().dateOfBirth());
        entity.setGradeLevel(student.gradeLevel().value());
        entity.setClassId(student.classId().value());
        entity.setStudentNumber(
                student.studentNumber().isEmpty()
                        ? null
                        : student.studentNumber().value());
        entity.setEnrollmentDate(student.enrollmentDate());
    }

    @Override
    public Optional<Student> findById(StudentId studentId) {
        PanacheStudentEntity entity = PanacheStudentEntity.findById(studentId.value());
        if (entity == null) {
            return Optional.empty();
        }
        return Optional.of(toDomain(entity));
    }

    @Override
    public List<Student> findByClassId(UserId classId) {
        List<PanacheStudentEntity> entities = PanacheStudentEntity.list("classId", classId.value());
        return entities.stream().map(this::toDomain).toList();
    }

    @Override
    public List<Student> findByGradeLevel(int gradeLevel) {
        List<PanacheStudentEntity> entities = PanacheStudentEntity.list("gradeLevel", gradeLevel);
        return entities.stream().map(this::toDomain).toList();
    }

    @Override
    public boolean existsById(StudentId studentId) {
        return PanacheStudentEntity.findById(studentId.value()) != null;
    }

    private PanacheStudentEntity toEntity(Student student) {
        PanacheStudentEntity entity = new PanacheStudentEntity();
        entity.setId(student.studentId().value());
        entity.setFirstName(student.personalInfo().firstName());
        entity.setLastName(student.personalInfo().lastName());
        entity.setDateOfBirth(student.personalInfo().dateOfBirth());
        entity.setGradeLevel(student.gradeLevel().value());
        entity.setClassId(student.classId().value());
        entity.setStudentNumber(
                student.studentNumber().isEmpty()
                        ? null
                        : student.studentNumber().value());
        entity.setEnrollmentDate(student.enrollmentDate());
        return entity;
    }

    private Student toDomain(PanacheStudentEntity entity) {
        StudentPersonalInfo personalInfo =
                StudentPersonalInfo.of(entity.getFirstName(), entity.getLastName(), entity.getDateOfBirth());

        StudentNumber studentNumber =
                entity.getStudentNumber() != null ? StudentNumber.of(entity.getStudentNumber()) : StudentNumber.empty();

        Instant createdAt =
                entity.getCreatedAt() != null ? entity.getCreatedAt().toInstant() : Instant.now();

        Instant updatedAt =
                entity.getUpdatedAt() != null ? entity.getUpdatedAt().toInstant() : Instant.now();

        return Student.reconstitute(
                StudentId.of(entity.getId()),
                personalInfo,
                GradeLevel.of(entity.getGradeLevel()),
                UserId.of(entity.getClassId()),
                studentNumber,
                entity.getEnrollmentDate(),
                createdAt,
                updatedAt);
    }
}
