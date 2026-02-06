package com.k12.platform.infrastructure.persistence;

import com.k12.platform.domain.model.Class;
import com.k12.platform.domain.model.ClassRepository;
import com.k12.platform.domain.model.valueobjects.*;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

/**
 * JPA-based implementation of ClassRepository port.
 */
@ApplicationScoped
public class JpaClassAdapter implements ClassRepository {

    @Override
    @Transactional
    public void save(Class clazz) {
        PanacheClassEntity existing =
                PanacheClassEntity.findById(clazz.getClassId().value());

        if (existing == null) {
            // Insert new entity
            PanacheClassEntity entity = toEntity(clazz);
            Instant now = Instant.now();
            entity.setCreatedAt(OffsetDateTime.ofInstant(now, ZoneOffset.UTC));
            entity.setUpdatedAt(OffsetDateTime.ofInstant(now, ZoneOffset.UTC));
            entity.persist();
        } else {
            // Update existing entity
            updateFromDomain(existing, clazz);
            existing.setUpdatedAt(OffsetDateTime.ofInstant(Instant.now(), ZoneOffset.UTC));
        }
    }

    @Override
    public Optional<Class> findById(ClassId classId) {
        PanacheClassEntity entity = PanacheClassEntity.findById(classId.value());
        if (entity == null) {
            return Optional.empty();
        }
        return Optional.of(toDomain(entity));
    }

    @Override
    public List<Class> findByGradeLevel(GradeLevel gradeLevel) {
        List<PanacheClassEntity> entities = PanacheClassEntity.list("gradeLevel", gradeLevel.value());
        return entities.stream().map(this::toDomain).toList();
    }

    @Override
    public List<Class> findByAcademicYear(AcademicYear academicYear) {
        List<PanacheClassEntity> entities = PanacheClassEntity.list("academicYear", academicYear.value());
        return entities.stream().map(this::toDomain).toList();
    }

    @Override
    public List<Class> findByGradeLevelAndAcademicYear(GradeLevel gradeLevel, AcademicYear academicYear) {
        List<PanacheClassEntity> entities = PanacheClassEntity.list(
                "gradeLevel = ?1 and academicYear = ?2", gradeLevel.value(), academicYear.value());
        return entities.stream().map(this::toDomain).toList();
    }

    @Override
    public boolean existsByNameAndGradeLevelAndAcademicYear(String name, int gradeLevel, String academicYear) {
        long count = PanacheClassEntity.count(
                "name = ?1 and gradeLevel = ?2 and academicYear = ?3", name, gradeLevel, academicYear);
        return count > 0;
    }

    @Override
    public boolean existsById(ClassId classId) {
        return PanacheClassEntity.findById(classId.value()) != null;
    }

    private PanacheClassEntity toEntity(Class clazz) {
        PanacheClassEntity entity = new PanacheClassEntity();
        entity.setId(clazz.getClassId().value());
        entity.setName(clazz.getName().value());
        entity.setGradeLevel(clazz.getGradeLevel().value());
        entity.setAcademicYear(clazz.getAcademicYear().value());
        return entity;
    }

    private void updateFromDomain(PanacheClassEntity entity, Class clazz) {
        entity.setName(clazz.getName().value());
        entity.setGradeLevel(clazz.getGradeLevel().value());
        entity.setAcademicYear(clazz.getAcademicYear().value());
    }

    private Class toDomain(PanacheClassEntity entity) {
        Instant createdAt =
                entity.getCreatedAt() != null ? entity.getCreatedAt().toInstant() : Instant.now();

        Instant updatedAt =
                entity.getUpdatedAt() != null ? entity.getUpdatedAt().toInstant() : Instant.now();

        return Class.reconstitute(
                ClassId.of(entity.getId()),
                ClassName.of(entity.getName()),
                GradeLevel.of(entity.getGradeLevel()),
                AcademicYear.of(entity.getAcademicYear()),
                createdAt,
                updatedAt);
    }
}
