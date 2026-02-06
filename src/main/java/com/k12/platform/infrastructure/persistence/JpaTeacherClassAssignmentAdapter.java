package com.k12.platform.infrastructure.persistence;

import com.k12.platform.domain.model.TeacherClassAssignment;
import com.k12.platform.domain.model.TeacherClassAssignmentRepository;
import com.k12.platform.domain.model.valueobjects.*;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

/**
 * JPA-based implementation of TeacherClassAssignmentRepository port.
 */
@ApplicationScoped
public class JpaTeacherClassAssignmentAdapter implements TeacherClassAssignmentRepository {

    @Override
    @Transactional
    public void save(TeacherClassAssignment assignment) {
        PanacheTeacherClassAssignmentEntity existing = PanacheTeacherClassAssignmentEntity.findById(
                assignment.assignmentId().value());

        if (existing == null) {
            PanacheTeacherClassAssignmentEntity entity = toEntity(assignment);
            entity.setCreatedAt(OffsetDateTime.ofInstant(Instant.now(), ZoneOffset.UTC));
            entity.persist();
        } else {
            updateFromDomain(existing, assignment);
        }
    }

    @Override
    public Optional<TeacherClassAssignment> findById(AssociationId assignmentId) {
        PanacheTeacherClassAssignmentEntity entity = PanacheTeacherClassAssignmentEntity.findById(assignmentId.value());
        if (entity == null) {
            return Optional.empty();
        }
        return Optional.of(toDomain(entity));
    }

    @Override
    public List<TeacherClassAssignment> findByTeacherId(UserId teacherId) {
        List<PanacheTeacherClassAssignmentEntity> entities =
                PanacheTeacherClassAssignmentEntity.list("teacherId", teacherId.value());
        return entities.stream().map(this::toDomain).toList();
    }

    @Override
    public List<TeacherClassAssignment> findByClassId(ClassId classId) {
        List<PanacheTeacherClassAssignmentEntity> entities =
                PanacheTeacherClassAssignmentEntity.list("classId", classId.value());
        return entities.stream().map(this::toDomain).toList();
    }

    @Override
    public Optional<TeacherClassAssignment> findByTeacherIdAndClassId(UserId teacherId, ClassId classId) {
        PanacheTeacherClassAssignmentEntity entity = PanacheTeacherClassAssignmentEntity.find(
                        "teacherId = ?1 and classId = ?2", teacherId.value(), classId.value())
                .firstResult();
        if (entity == null) {
            return Optional.empty();
        }
        return Optional.of(toDomain(entity));
    }

    @Override
    public boolean existsByTeacherIdAndClassId(UserId teacherId, ClassId classId) {
        long count = PanacheTeacherClassAssignmentEntity.count(
                "teacherId = ?1 and classId = ?2", teacherId.value(), classId.value());
        return count > 0;
    }

    @Override
    @Transactional
    public void delete(AssociationId assignmentId) {
        PanacheTeacherClassAssignmentEntity.deleteById(assignmentId.value());
    }

    private PanacheTeacherClassAssignmentEntity toEntity(TeacherClassAssignment assignment) {
        PanacheTeacherClassAssignmentEntity entity = new PanacheTeacherClassAssignmentEntity();
        entity.setId(assignment.assignmentId().value());
        entity.setTeacherId(assignment.teacherId().value());
        entity.setClassId(assignment.classId().value());
        entity.setRole(assignment.role().value());
        entity.setAssignedDate(assignment.assignedDate());
        return entity;
    }

    private void updateFromDomain(PanacheTeacherClassAssignmentEntity entity, TeacherClassAssignment assignment) {
        entity.setRole(assignment.role().value());
    }

    private TeacherClassAssignment toDomain(PanacheTeacherClassAssignmentEntity entity) {
        Instant createdAt =
                entity.getCreatedAt() != null ? entity.getCreatedAt().toInstant() : Instant.now();

        return TeacherClassAssignment.reconstitute(
                AssociationId.of(entity.getId()),
                UserId.of(entity.getTeacherId()),
                ClassId.of(entity.getClassId()),
                TeacherRole.of(entity.getRole()),
                entity.getAssignedDate(),
                createdAt);
    }
}
