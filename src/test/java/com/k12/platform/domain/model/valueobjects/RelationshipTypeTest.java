package com.k12.platform.domain.model.valueobjects;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for RelationshipType value object.
 * Target: 100% coverage
 */
@DisplayName("RelationshipType Value Object Tests")
class RelationshipTypeTest {

    @Test
    @DisplayName("Should create valid relationship type")
    void shouldCreateValidRelationshipType() {
        RelationshipType type = RelationshipType.of("Stepfather");
        assertEquals("Stepfather", type.value());
    }

    @Test
    @DisplayName("Should reject null type")
    void shouldRejectNullType() {
        assertThrows(IllegalArgumentException.class, () -> RelationshipType.of(null));
    }

    @Test
    @DisplayName("Should reject blank type")
    void shouldRejectBlankType() {
        assertThrows(IllegalArgumentException.class, () -> RelationshipType.of("   "));
    }

    @Test
    @DisplayName("Should reject type over 50 characters")
    void shouldRejectTypeOver50Characters() {
        String tooLong = "A".repeat(51);
        assertThrows(IllegalArgumentException.class, () -> RelationshipType.of(tooLong));
    }

    @Test
    @DisplayName("Should create father type")
    void shouldCreateFatherType() {
        RelationshipType type = RelationshipType.father();
        assertEquals("Father", type.value());
    }

    @Test
    @DisplayName("Should create mother type")
    void shouldCreateMotherType() {
        RelationshipType type = RelationshipType.mother();
        assertEquals("Mother", type.value());
    }

    @Test
    @DisplayName("Should create guardian type")
    void shouldCreateGuardianType() {
        RelationshipType type = RelationshipType.guardian();
        assertEquals("Guardian", type.value());
    }

    @Test
    @DisplayName("Should create stepfather type")
    void shouldCreateStepfatherType() {
        RelationshipType type = RelationshipType.stepfather();
        assertEquals("Stepfather", type.value());
    }

    @Test
    @DisplayName("Should create stepmother type")
    void shouldCreateStepmotherType() {
        RelationshipType type = RelationshipType.stepmother();
        assertEquals("Stepmother", type.value());
    }

    @Test
    @DisplayName("Should create grandparent type")
    void shouldCreateGrandparentType() {
        RelationshipType type = RelationshipType.grandparent();
        assertEquals("Grandparent", type.value());
    }

    @Test
    @DisplayName("Should trim whitespace")
    void shouldTrimWhitespace() {
        RelationshipType type = RelationshipType.of("  Guardian  ");
        assertEquals("Guardian", type.value());
    }

    @Test
    @DisplayName("Should consider same types as equal")
    void shouldConsiderSameTypesAsEqual() {
        RelationshipType type1 = RelationshipType.of("Father");
        RelationshipType type2 = RelationshipType.of("Father");

        assertEquals(type1, type2);
        assertEquals(type1.hashCode(), type2.hashCode());
    }

    @Test
    @DisplayName("Should consider different types as not equal")
    void shouldConsiderDifferentTypesAsNotEqual() {
        RelationshipType type1 = RelationshipType.father();
        RelationshipType type2 = RelationshipType.mother();

        assertNotEquals(type1, type2);
    }

    @Test
    @DisplayName("Should convert to string correctly")
    void shouldConvertToStringCorrectly() {
        RelationshipType type = RelationshipType.of("Father");
        assertEquals("Father", type.toString());
    }
}
