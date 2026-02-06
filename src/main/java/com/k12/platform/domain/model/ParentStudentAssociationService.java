package com.k12.platform.domain.model;

import com.k12.platform.domain.model.valueobjects.AssociationId;
import com.k12.platform.domain.model.valueobjects.RelationshipType;
import com.k12.platform.domain.model.valueobjects.StudentId;
import com.k12.platform.domain.model.valueobjects.UserId;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * Domain service for Parent-Student association operations.
 */
@ApplicationScoped
public class ParentStudentAssociationService {

    private final ParentStudentAssociationRepository repository;

    public ParentStudentAssociationService(ParentStudentAssociationRepository repository) {
        this.repository = repository;
    }

    /**
     * Create a new parent-student association with uniqueness validation.
     */
    public ParentStudentAssociation associate(
            UserId parentId, StudentId studentId, RelationshipType relationshipType, boolean isPrimaryContact) {
        // Check for uniqueness
        if (repository.existsByParentIdAndStudentId(parentId, studentId)) {
            throw new IllegalStateException(
                    "Association already exists between parent " + parentId + " and student " + studentId);
        }

        ParentStudentAssociation association =
                ParentStudentAssociation.create(parentId, studentId, relationshipType, isPrimaryContact);

        repository.save(association);
        return association;
    }

    /**
     * Set parent as primary contact for student.
     */
    public void setAsPrimaryContact(AssociationId associationId) {
        ParentStudentAssociation association = repository
                .findById(associationId)
                .orElseThrow(() -> new IllegalArgumentException("Association not found"));

        association.setAsPrimaryContact();
        repository.save(association);
    }
}
