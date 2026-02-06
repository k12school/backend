package com.k12.platform.domain.model;

import static org.junit.jupiter.api.Assertions.*;

import com.k12.platform.domain.model.valueobjects.AssociationId;
import com.k12.platform.domain.model.valueobjects.RelationshipType;
import com.k12.platform.domain.model.valueobjects.StudentId;
import com.k12.platform.domain.model.valueobjects.UserId;
import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for ParentStudentAssociation aggregate.
 * Target: 100% coverage
 */
@DisplayName("ParentStudentAssociation Aggregate Tests")
class ParentStudentAssociationTest {

    @Test
    @DisplayName("Should create parent-student association")
    void shouldCreateAssociation() {
        UserId parentId = UserId.generate();
        StudentId studentId = StudentId.generate();
        RelationshipType relationshipType = RelationshipType.father();
        boolean isPrimaryContact = true;

        ParentStudentAssociation association =
                ParentStudentAssociation.create(parentId, studentId, relationshipType, isPrimaryContact);

        assertNotNull(association.associationId());
        assertEquals(parentId, association.parentId());
        assertEquals(studentId, association.studentId());
        assertEquals(relationshipType, association.relationshipType());
        assertEquals(isPrimaryContact, association.isPrimaryContact());
        assertNotNull(association.createdAt());
    }

    @Test
    @DisplayName("Should record ParentStudentAssociated event on creation")
    void shouldRecordParentStudentAssociatedEvent() {
        UserId parentId = UserId.generate();
        StudentId studentId = StudentId.generate();
        RelationshipType relationshipType = RelationshipType.mother();

        ParentStudentAssociation association =
                ParentStudentAssociation.create(parentId, studentId, relationshipType, false);

        var domainEvents = association.getDomainEvents();
        assertEquals(1, domainEvents.size());
        assertTrue(domainEvents.get(0) instanceof ParentStudentAssociated);
    }

    @Test
    @DisplayName("Should create non-primary contact association")
    void shouldCreateNonPrimaryContactAssociation() {
        UserId parentId = UserId.generate();
        StudentId studentId = StudentId.generate();
        RelationshipType relationshipType = RelationshipType.guardian();

        ParentStudentAssociation association =
                ParentStudentAssociation.create(parentId, studentId, relationshipType, false);

        assertFalse(association.isPrimaryContact());
    }

    @Test
    @DisplayName("Should set as primary contact")
    void shouldSetAsPrimaryContact() {
        UserId parentId = UserId.generate();
        StudentId studentId = StudentId.generate();
        RelationshipType relationshipType = RelationshipType.father();

        ParentStudentAssociation association =
                ParentStudentAssociation.create(parentId, studentId, relationshipType, false);

        assertFalse(association.isPrimaryContact());

        association.setAsPrimaryContact();

        assertTrue(association.isPrimaryContact());
    }

    @Test
    @DisplayName("Should remove as primary contact")
    void shouldRemoveAsPrimaryContact() {
        UserId parentId = UserId.generate();
        StudentId studentId = StudentId.generate();
        RelationshipType relationshipType = RelationshipType.mother();

        ParentStudentAssociation association =
                ParentStudentAssociation.create(parentId, studentId, relationshipType, true);

        assertTrue(association.isPrimaryContact());

        association.removeAsPrimaryContact();

        assertFalse(association.isPrimaryContact());
    }

    @Test
    @DisplayName("Should reconstitute association from persistence")
    void shouldReconstituteAssociationFromPersistence() {
        AssociationId associationId = AssociationId.generate();
        UserId parentId = UserId.generate();
        StudentId studentId = StudentId.generate();
        RelationshipType relationshipType = RelationshipType.stepfather();
        boolean isPrimaryContact = false;
        Instant createdAt = Instant.now().minusSeconds(3600);

        ParentStudentAssociation association = ParentStudentAssociation.reconstitute(
                associationId, parentId, studentId, relationshipType, isPrimaryContact, createdAt);

        assertEquals(associationId, association.associationId());
        assertEquals(parentId, association.parentId());
        assertEquals(studentId, association.studentId());
        assertEquals(relationshipType, association.relationshipType());
        assertEquals(isPrimaryContact, association.isPrimaryContact());
        assertEquals(0, association.getDomainEvents().size()); // No events on reconstitution
    }

    @Test
    @DisplayName("Should get domain events")
    void shouldGetDomainEvents() {
        UserId parentId = UserId.generate();
        StudentId studentId = StudentId.generate();
        RelationshipType relationshipType = RelationshipType.grandparent();

        ParentStudentAssociation association =
                ParentStudentAssociation.create(parentId, studentId, relationshipType, false);

        var domainEvents = association.getDomainEvents();
        assertFalse(domainEvents.isEmpty());
        assertTrue(domainEvents.get(0) instanceof ParentStudentAssociated);
    }

    @Test
    @DisplayName("Should have unmodifiable domain events")
    void shouldHaveUnmodifiableDomainEvents() {
        UserId parentId = UserId.generate();
        StudentId studentId = StudentId.generate();
        RelationshipType relationshipType = RelationshipType.father();

        ParentStudentAssociation association =
                ParentStudentAssociation.create(parentId, studentId, relationshipType, true);
        var domainEvents = association.getDomainEvents();

        assertThrows(UnsupportedOperationException.class, () -> domainEvents.clear());
    }

    @Test
    @DisplayName("Should handle all relationship types")
    void shouldHandleAllRelationshipTypes() {
        UserId parentId = UserId.generate();
        StudentId studentId = StudentId.generate();

        RelationshipType[] types = {
            RelationshipType.father(),
            RelationshipType.mother(),
            RelationshipType.guardian(),
            RelationshipType.stepfather(),
            RelationshipType.stepmother(),
            RelationshipType.grandparent()
        };

        for (RelationshipType type : types) {
            ParentStudentAssociation association = ParentStudentAssociation.create(parentId, studentId, type, false);
            assertEquals(type, association.relationshipType());
        }
    }

    @Test
    @DisplayName("Should have unique association IDs")
    void shouldHaveUniqueAssociationIds() {
        UserId parentId = UserId.generate();
        StudentId studentId = StudentId.generate();
        RelationshipType relationshipType = RelationshipType.father();

        ParentStudentAssociation association1 =
                ParentStudentAssociation.create(parentId, studentId, relationshipType, true);
        ParentStudentAssociation association2 =
                ParentStudentAssociation.create(parentId, studentId, relationshipType, false);

        assertNotEquals(association1.associationId(), association2.associationId());
    }

    @Test
    @DisplayName("Should toggle primary contact status")
    void shouldTogglePrimaryContactStatus() {
        UserId parentId = UserId.generate();
        StudentId studentId = StudentId.generate();
        RelationshipType relationshipType = RelationshipType.mother();

        ParentStudentAssociation association =
                ParentStudentAssociation.create(parentId, studentId, relationshipType, false);

        assertFalse(association.isPrimaryContact());

        association.setAsPrimaryContact();
        assertTrue(association.isPrimaryContact());

        association.removeAsPrimaryContact();
        assertFalse(association.isPrimaryContact());
    }

    @Test
    @DisplayName("Should set primary contact multiple times")
    void shouldSetPrimaryContactMultipleTimes() {
        UserId parentId = UserId.generate();
        StudentId studentId = StudentId.generate();
        RelationshipType relationshipType = RelationshipType.guardian();

        ParentStudentAssociation association =
                ParentStudentAssociation.create(parentId, studentId, relationshipType, false);

        association.setAsPrimaryContact();
        assertTrue(association.isPrimaryContact());

        association.setAsPrimaryContact(); // Should still be true
        assertTrue(association.isPrimaryContact());
    }

    @Test
    @DisplayName("Should remove primary contact multiple times")
    void shouldRemovePrimaryContactMultipleTimes() {
        UserId parentId = UserId.generate();
        StudentId studentId = StudentId.generate();
        RelationshipType relationshipType = RelationshipType.father();

        ParentStudentAssociation association =
                ParentStudentAssociation.create(parentId, studentId, relationshipType, true);

        association.removeAsPrimaryContact();
        assertFalse(association.isPrimaryContact());

        association.removeAsPrimaryContact(); // Should still be false
        assertFalse(association.isPrimaryContact());
    }
}
