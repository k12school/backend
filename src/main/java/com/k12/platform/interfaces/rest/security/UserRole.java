package com.k12.platform.interfaces.rest.security;

/**
 * User roles for Role-Based Access Control (RBAC).
 * Matches domain model UserRole but kept separate to avoid coupling layers.
 */
public enum UserRole {
    /**
     * Administrator - full system access.
     * Can manage all users, classes, students, and associations.
     */
    ADMIN,

    /**
     * Teacher - limited to assigned classes and students.
     * Can view/manage students in their assigned classes.
     */
    TEACHER,

    /**
     * Parent - limited to linked children.
     * Can only view their own children's data.
     */
    PARENT;

    /**
     * Parse role from string (case-insensitive).
     */
    public static UserRole fromString(String role) {
        return valueOf(role.toUpperCase());
    }

    /**
     * Check if this role is admin.
     */
    public boolean isAdmin() {
        return this == ADMIN;
    }

    /**
     * Check if this role is teacher.
     */
    public boolean isTeacher() {
        return this == TEACHER;
    }

    /**
     * Check if this role is parent.
     */
    public boolean isParent() {
        return this == PARENT;
    }
}
