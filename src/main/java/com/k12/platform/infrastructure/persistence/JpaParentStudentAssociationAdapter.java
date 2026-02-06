package com.k12.platform.infrastructure.persistence;

import com.k12.platform.domain.model.ParentStudentAssociation;
import com.k12.platform.domain.model.ParentStudentAssociationRepository;
import com.k12.platform.domain.model.valueobjects.*;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

/**
 * JPA-based implementation of ParentStudentAssociationRepository port.
 */
@ApplicationScoped
public class JpaParentStudentAssociationAdapter implements ParentStudentAssociationRepository {

    @Override
    @Transactional
    public void save(ParentStudentAssociation association) {
        PanacheParentStudentAssociationEntity existing = PanacheParentStudentAssociationEntity.findById(
                association.associationId().value());

        if (existing == null) {
            PanacheParentStudentAssociationEntity entity = toEntity(association);
            entity.setCreatedAt(OffsetDateTime.ofInstant(Instant.now(), ZoneOffset.UTC));
            entity.persist();
        } else {
            updateFromDomain(existing, association);
        }
    }

    @Override
    public Optional<ParentStudentAssociation> findById(AssociationId associationId) {
        PanacheParentStudentAssociationEntity entity =
                PanacheParentStudentAssociationEntity.findById(associationId.value());
        if (entity == null) {
            return Optional.empty();
        }
        return Optional.of(toDomain(entity));
    }

    @Override
    public List<ParentStudentAssociation> findByParentId(UserId parentId) {
        List<PanacheParentStudentAssociationEntity> entities =
                PanacheParentStudentAssociationEntity.list("parentId", parentId.value());
        return entities.stream().map(this::toDomain).toList();
    }

    @Override
    public List<ParentStudentAssociation> findByStudentId(StudentId studentId) {
        List<PanacheParentStudentAssociationEntity> entities =
                PanacheParentStudentAssociationEntity.list("studentId", studentId.value());
        return entities.stream().map(this::toDomain).toList();
    }

    @Override
    public Optional<ParentStudentAssociation> findByParentIdAndStudentId(UserId parentId, StudentId studentId) {
        PanacheParentStudentAssociationEntity entity = PanacheParentStudentAssociationEntity.find(
                        "parentId = ?1 and studentId = ?2", parentId.value(), studentId.value())
                .firstResult();
        if (entity == null) {
            return Optional.empty();
        }
        return Optional.of(toDomain(entity));
    }

    @Override
    public boolean existsByParentIdAndStudentId(UserId parentId, StudentId studentId) {
        long count = PanacheParentStudentAssociationEntity.count(
                "parentId = ?1 and studentId = ?2", parentId.value(), studentId.value());
        return count > 0;
    }

    @Override
    @Transactional
    public void delete(AssociationId associationId) {
        PanacheParentStudentAssociationEntity.deleteById(associationId.value());
    }

    private PanacheParentStudentAssociationEntity toEntity(ParentStudentAssociation association) {
        PanacheParentStudentAssociationEntity entity = new PanacheParentStudentAssociationEntity();
        entity.setId(association.associationId().value());
        entity.setParentId(association.parentId().value());
        entity.setStudentId(association.studentId().value());
        entity.setRelationshipType(association.relationshipType().value());
        entity.setPrimaryContact(association.isPrimaryContact());
        return entity;
    }

    private void updateFromDomain(PanacheParentStudentAssociationEntity entity, ParentStudentAssociation association) {
        entity.setRelationshipType(association.relationshipType().value());
        entity.setPrimaryContact(association.isPrimaryContact());
    }

    private ParentStudentAssociation toDomain(PanacheParentStudentAssociationEntity entity) {
        Instant createdAt =
                entity.getCreatedAt() != null ? entity.getCreatedAt().toInstant() : Instant.now();

        return ParentStudentAssociation.reconstitute(
                AssociationId.of(entity.getId()),
                UserId.of(entity.getParentId()),
                StudentId.of(entity.getStudentId()),
                RelationshipType.of(entity.getRelationshipType()),
                entity.isPrimaryContact(),
                createdAt);
    }
}
