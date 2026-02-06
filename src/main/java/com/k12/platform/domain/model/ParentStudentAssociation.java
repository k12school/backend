package com.k12.platform.domain.model;

import com.k12.platform.domain.model.valueobjects.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Parent-Student association aggregate root.
 * Represents the relationship between a parent user and a student.
 *
 * IMPORTANT: This is PURE JAVA domain model - NO framework imports allowed.
 */
public final class ParentStudentAssociation {

    private final AssociationId associationId;
    private final UserId parentId;
    private final StudentId studentId;
    private final RelationshipType relationshipType;
    private boolean isPrimaryContact;
    private final Instant createdAt;

    private ParentStudentAssociation(
            AssociationId associationId,
            UserId parentId,
            StudentId studentId,
            RelationshipType relationshipType,
            boolean isPrimaryContact) {
        this.associationId = associationId;
        this.parentId = parentId;
        this.studentId = studentId;
        this.relationshipType = relationshipType;
        this.isPrimaryContact = isPrimaryContact;
        this.createdAt = Instant.now();
    }

    /**
     * Factory method to create a new parent-student association.
     */
    public static ParentStudentAssociation create(
            UserId parentId, StudentId studentId, RelationshipType relationshipType, boolean isPrimaryContact) {
        ParentStudentAssociation association = new ParentStudentAssociation(
                AssociationId.generate(), parentId, studentId, relationshipType, isPrimaryContact);

        association.recordDomainEvent(new ParentStudentAssociated(
                association.associationId,
                association.parentId,
                association.studentId,
                association.relationshipType,
                null));

        return association;
    }

    /**
     * Factory method to reconstitute from persistence.
     */
    public static ParentStudentAssociation reconstitute(
            AssociationId associationId,
            UserId parentId,
            StudentId studentId,
            RelationshipType relationshipType,
            boolean isPrimaryContact,
            Instant createdAt) {
        ParentStudentAssociation association =
                new ParentStudentAssociation(associationId, parentId, studentId, relationshipType, isPrimaryContact);
        return association;
    }

    /**
     * Set as primary contact.
     */
    public void setAsPrimaryContact() {
        this.isPrimaryContact = true;
    }

    /**
     * Remove as primary contact.
     */
    public void removeAsPrimaryContact() {
        this.isPrimaryContact = false;
    }

    private void recordDomainEvent(Object event) {
        this.domainEvents.add(event);
    }

    private final List<Object> domainEvents = new ArrayList<>();

    public List<Object> getDomainEvents() {
        return Collections.unmodifiableList(domainEvents);
    }

    public void clearDomainEvents() {
        this.domainEvents.clear();
    }

    // Getters
    public AssociationId associationId() {
        return associationId;
    }

    public UserId parentId() {
        return parentId;
    }

    public StudentId studentId() {
        return studentId;
    }

    public RelationshipType relationshipType() {
        return relationshipType;
    }

    public boolean isPrimaryContact() {
        return isPrimaryContact;
    }

    public Instant createdAt() {
        return createdAt;
    }
}
