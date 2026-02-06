package com.k12.platform.domain.model;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.k12.platform.domain.model.valueobjects.AssociationId;
import com.k12.platform.domain.model.valueobjects.RelationshipType;
import com.k12.platform.domain.model.valueobjects.StudentId;
import com.k12.platform.domain.model.valueobjects.UserId;
import java.util.Optional;

import com.k12.platform.domain.port.ParentStudentAssociationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Unit tests for ParentStudentAssociationService domain service.
 * Target: 90%+ coverage
 */
@DisplayName("ParentStudentAssociationService Tests")
class ParentStudentAssociationServiceTest {

    @Mock
    ParentStudentAssociationRepository repository;

    ParentStudentAssociationService service;

    private UserId parentId;
    private StudentId studentId;
    private RelationshipType relationshipType;
    private AssociationId associationId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new ParentStudentAssociationService(repository);
        parentId = UserId.generate();
        studentId = StudentId.generate();
        relationshipType = RelationshipType.father();
        associationId = AssociationId.generate();
        doNothing().when(repository).save(any(ParentStudentAssociation.class));
    }

    @Test
    @DisplayName("Should create association successfully")
    void shouldCreateAssociationSuccessfully() {
        when(repository.existsByParentIdAndStudentId(parentId, studentId)).thenReturn(false);

        ParentStudentAssociation result = service.associate(parentId, studentId, relationshipType, true);

        assertNotNull(result);
        assertEquals(parentId, result.parentId());
        assertEquals(studentId, result.studentId());
        assertEquals(relationshipType, result.relationshipType());
        assertTrue(result.isPrimaryContact());
        verify(repository).existsByParentIdAndStudentId(parentId, studentId);
        verify(repository).save(any(ParentStudentAssociation.class));
    }

    @Test
    @DisplayName("Should throw IllegalStateException when association already exists")
    void shouldThrowIllegalStateExceptionWhenAssociationExists() {
        when(repository.existsByParentIdAndStudentId(parentId, studentId)).thenReturn(true);

        IllegalStateException exception = assertThrows(
                IllegalStateException.class, () -> service.associate(parentId, studentId, relationshipType, false));

        assertTrue(exception.getMessage().contains("already exists"));
        verify(repository).existsByParentIdAndStudentId(parentId, studentId);
        verify(repository, never()).save(any(ParentStudentAssociation.class));
    }

    @Test
    @DisplayName("Should not save when association already exists")
    void shouldNotSaveWhenAssociationExists() {
        when(repository.existsByParentIdAndStudentId(parentId, studentId)).thenReturn(true);

        assertThrows(
                IllegalStateException.class, () -> service.associate(parentId, studentId, relationshipType, false));

        verify(repository, never()).save(any(ParentStudentAssociation.class));
    }

    @Test
    @DisplayName("Should set parent as primary contact")
    void shouldSetParentAsPrimaryContact() {
        ParentStudentAssociation association =
                ParentStudentAssociation.create(parentId, studentId, relationshipType, false);
        when(repository.findById(associationId)).thenReturn(Optional.of(association));

        service.setAsPrimaryContact(associationId);

        assertTrue(association.isPrimaryContact());
        verify(repository).findById(associationId);
        verify(repository).save(association);
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when association not found")
    void shouldThrowIllegalArgumentExceptionWhenAssociationNotFound() {
        when(repository.findById(associationId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> service.setAsPrimaryContact(associationId));

        verify(repository).findById(associationId);
        verify(repository, never()).save(any(ParentStudentAssociation.class));
    }

    @Test
    @DisplayName("Should check uniqueness before creating association")
    void shouldCheckUniquenessBeforeCreatingAssociation() {
        when(repository.existsByParentIdAndStudentId(parentId, studentId)).thenReturn(false);

        service.associate(parentId, studentId, relationshipType, false);

        verify(repository).existsByParentIdAndStudentId(parentId, studentId);
    }

    @Test
    @DisplayName("Should save association after creation")
    void shouldSaveAssociationAfterCreation() {
        when(repository.existsByParentIdAndStudentId(parentId, studentId)).thenReturn(false);

        service.associate(parentId, studentId, relationshipType, false);

        verify(repository).save(any(ParentStudentAssociation.class));
    }

    @Test
    @DisplayName("Should save after setting as primary contact")
    void shouldSaveAfterSettingAsPrimaryContact() {
        ParentStudentAssociation association =
                ParentStudentAssociation.create(parentId, studentId, relationshipType, false);
        when(repository.findById(associationId)).thenReturn(Optional.of(association));

        service.setAsPrimaryContact(associationId);

        verify(repository).save(association);
    }

    @Test
    @DisplayName("Should handle non-primary contact association")
    void shouldHandleNonPrimaryContactAssociation() {
        when(repository.existsByParentIdAndStudentId(parentId, studentId)).thenReturn(false);

        ParentStudentAssociation result = service.associate(parentId, studentId, relationshipType, false);

        assertFalse(result.isPrimaryContact());
    }

    @Test
    @DisplayName("Should handle all relationship types")
    void shouldHandleAllRelationshipTypes() {
        RelationshipType[] types = {RelationshipType.father(), RelationshipType.mother(), RelationshipType.guardian()};

        for (RelationshipType type : types) {
            when(repository.existsByParentIdAndStudentId(parentId, studentId)).thenReturn(false);

            ParentStudentAssociation result = service.associate(parentId, studentId, type, false);

            assertEquals(type, result.relationshipType());
        }
    }
}
