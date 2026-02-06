package com.k12.platform.domain.model.valueobjects;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for TeacherRole value object.
 * Target: 100% coverage
 */
@DisplayName("TeacherRole Value Object Tests")
class TeacherRoleTest {

    @Test
    @DisplayName("Should create valid teacher role")
    void shouldCreateValidTeacherRole() {
        TeacherRole role = TeacherRole.of("Subject Teacher");
        assertEquals("Subject Teacher", role.value());
    }

    @Test
    @DisplayName("Should reject null role")
    void shouldRejectNullRole() {
        assertThrows(IllegalArgumentException.class, () -> TeacherRole.of(null));
    }

    @Test
    @DisplayName("Should reject blank role")
    void shouldRejectBlankRole() {
        assertThrows(IllegalArgumentException.class, () -> TeacherRole.of("   "));
    }

    @Test
    @DisplayName("Should reject role over 50 characters")
    void shouldRejectRoleOver50Characters() {
        String tooLong = "A".repeat(51);
        assertThrows(IllegalArgumentException.class, () -> TeacherRole.of(tooLong));
    }

    @Test
    @DisplayName("Should create homeroom teacher role")
    void shouldCreateHomeroomTeacherRole() {
        TeacherRole role = TeacherRole.homeroomTeacher();
        assertEquals("Homeroom Teacher", role.value());
    }

    @Test
    @DisplayName("Should create subject teacher role")
    void shouldCreateSubjectTeacherRole() {
        TeacherRole role = TeacherRole.subjectTeacher();
        assertEquals("Subject Teacher", role.value());
    }

    @Test
    @DisplayName("Should create assistant teacher role")
    void shouldCreateAssistantTeacherRole() {
        TeacherRole role = TeacherRole.assistantTeacher();
        assertEquals("Assistant Teacher", role.value());
    }

    @Test
    @DisplayName("Should trim whitespace")
    void shouldTrimWhitespace() {
        TeacherRole role = TeacherRole.of("  Subject Teacher  ");
        assertEquals("Subject Teacher", role.value());
    }

    @Test
    @DisplayName("Should consider same roles as equal")
    void shouldConsiderSameRolesAsEqual() {
        TeacherRole role1 = TeacherRole.of("Homeroom Teacher");
        TeacherRole role2 = TeacherRole.of("Homeroom Teacher");

        assertEquals(role1, role2);
        assertEquals(role1.hashCode(), role2.hashCode());
    }

    @Test
    @DisplayName("Should consider different roles as not equal")
    void shouldConsiderDifferentRolesAsNotEqual() {
        TeacherRole role1 = TeacherRole.homeroomTeacher();
        TeacherRole role2 = TeacherRole.subjectTeacher();

        assertNotEquals(role1, role2);
    }

    @Test
    @DisplayName("Should convert to string correctly")
    void shouldConvertToStringCorrectly() {
        TeacherRole role = TeacherRole.of("Homeroom Teacher");
        assertEquals("Homeroom Teacher", role.toString());
    }

    @Test
    @DisplayName("Should accept exactly 50 characters")
    void shouldAcceptExactly50Characters() {
        String exactly50 = "A".repeat(50);
        TeacherRole role = TeacherRole.of(exactly50);
        assertEquals(exactly50, role.value());
    }
}
