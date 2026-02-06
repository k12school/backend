package com.k12.platform.interfaces.rest.security;

import com.k12.platform.domain.model.ClassRepository;
import com.k12.platform.domain.model.ParentStudentAssociation;
import com.k12.platform.domain.model.ParentStudentAssociationRepository;
import com.k12.platform.domain.model.Student;
import com.k12.platform.domain.model.StudentRepository;
import com.k12.platform.domain.model.TeacherClassAssignment;
import com.k12.platform.domain.model.TeacherClassAssignmentRepository;
import com.k12.platform.domain.model.valueobjects.ClassId;
import com.k12.platform.domain.model.valueobjects.StudentId;
import com.k12.platform.domain.model.valueobjects.UserId;
import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import java.util.*;
import java.util.stream.Collectors;
import org.eclipse.microprofile.jwt.JsonWebToken;

/**
 * Security interceptor that enforces @RequireRole annotations.
 * Checks:
 * <ol>
 * <li>User is authenticated</li>
 * <li>User has required role(s)</li>
 * <li>User owns the resource (for non-admins)</li>
 * </ol>
 */
@Interceptor
@RequireRole
@Priority(2000)
public class RoleBasedSecurityInterceptor {

    @Inject
    ClassRepository classRepository;

    @Inject
    StudentRepository studentRepository;

    @Inject
    TeacherClassAssignmentRepository teacherClassAssignmentRepository;

    @Inject
    ParentStudentAssociationRepository parentStudentAssociationRepository;

    @Context
    SecurityContext securityContext;

    @AroundInvoke
    public Object enforceRoleBasedAccess(InvocationContext context) throws Exception {
        JsonWebToken jwt = (JsonWebToken) securityContext.getUserPrincipal();
        if (jwt == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"message\":\"Authentication required\"}")
                    .build();
        }

        Collection<String> userRoles = getRolesFromToken(jwt);
        if (userRoles.isEmpty()) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity("{\"message\":\"No roles found in token\"}")
                    .build();
        }

        RequireRole annotation = context.getMethod().getAnnotation(RequireRole.class);
        if (annotation == null) {
            annotation = context.getTarget().getClass().getAnnotation(RequireRole.class);
        }

        UserRole[] requiredRoles = annotation.value();
        boolean requireAll = annotation.requireAll();

        boolean hasRequiredRole = checkRoles(userRoles, requiredRoles, requireAll);

        if (!hasRequiredRole) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity("{\"message\":\"Insufficient permissions: requires role(s) "
                            + Arrays.stream(requiredRoles).map(UserRole::name).collect(Collectors.joining(", "))
                            + "}\"")
                    .build();
        }

        if (!userRoles.contains("ADMIN")) {
            String ownershipError = checkResourceOwnership(context, userRoles);
            if (ownershipError != null) {
                return Response.status(Response.Status.FORBIDDEN)
                        .entity("{\"message\":\"" + ownershipError + "\"}")
                        .build();
            }
        }

        return context.proceed();
    }

    /**
     * Extract roles from JWT token.
     */
    private Collection<String> getRolesFromToken(JsonWebToken jwt) {
        Object groups = jwt.getClaim("groups");
        if (groups instanceof Collection) {
            return (Collection<String>) groups;
        }
        if (groups instanceof String) {
            return Collections.singletonList((String) groups);
        }
        return Collections.emptyList();
    }

    /**
     * Check if user has required roles.
     */
    private boolean checkRoles(Collection<String> userRoles, UserRole[] requiredRoles, boolean requireAll) {
        if (requiredRoles.length == 0) {
            return true;
        }

        Set<String> requiredRoleNames =
                Arrays.stream(requiredRoles).map(UserRole::name).collect(Collectors.toSet());

        if (requireAll) {
            return userRoles.containsAll(requiredRoleNames);
        } else {
            return userRoles.stream().anyMatch(requiredRoleNames::contains);
        }
    }

    /**
     * Check resource ownership for non-admin users.
     * Returns error message if access denied, null if allowed.
     */
    private String checkResourceOwnership(InvocationContext context, Collection<String> userRoles) {
        String userIdStr = securityContext.getUserPrincipal().getName();
        UserId userId = UserId.of(UUID.fromString(userIdStr));

        Object[] parameters = context.getParameters();

        if (userRoles.contains("TEACHER")) {
            return checkTeacherResourceAccess(
                    userId, parameters, context.getMethod().getName());
        }

        if (userRoles.contains("PARENT")) {
            return checkParentResourceAccess(
                    userId, parameters, context.getMethod().getName());
        }

        return "Invalid user role";
    }

    /**
     * Check if teacher has access to requested resources.
     */
    private String checkTeacherResourceAccess(UserId teacherId, Object[] parameters, String methodName) {
        UUID classId = null;
        UUID studentId = null;

        for (Object param : parameters) {
            if (param instanceof UUID) {
                if (methodName.contains("Class") || methodName.toLowerCase().contains("class")) {
                    classId = (UUID) param;
                } else if (methodName.contains("Student")
                        || methodName.toLowerCase().contains("student")) {
                    studentId = (UUID) param;
                }
            } else if (param instanceof String) {
                try {
                    UUID uuid = UUID.fromString((String) param);
                    if (methodName.contains("Class")) {
                        classId = uuid;
                    } else if (methodName.contains("Student")) {
                        studentId = uuid;
                    }
                } catch (IllegalArgumentException ignored) {
                    // Not a UUID
                }
            }
        }

        if (classId != null) {
            if (!isTeacherAssignedToClass(teacherId, ClassId.of(classId))) {
                return "Teacher not assigned to this class";
            }
        }

        if (studentId != null) {
            if (!isStudentInTeacherClass(teacherId, StudentId.of(studentId))) {
                return "Student not in teacher's assigned class";
            }
        }

        return null;
    }

    /**
     * Check if parent has access to requested resources.
     */
    private String checkParentResourceAccess(UserId parentId, Object[] parameters, String methodName) {
        UUID studentId = null;

        for (Object param : parameters) {
            if (param instanceof UUID) {
                if (methodName.contains("Student")) {
                    studentId = (UUID) param;
                }
            } else if (param instanceof String) {
                try {
                    UUID uuid = UUID.fromString((String) param);
                    if (methodName.contains("Student")) {
                        studentId = uuid;
                    }
                } catch (IllegalArgumentException ignored) {
                    // Not a UUID
                }
            }
        }

        if (studentId != null) {
            if (!isParentLinkedToStudent(parentId, StudentId.of(studentId))) {
                return "Parent not linked to this student";
            }
        }

        return null;
    }

    /**
     * Check if teacher is assigned to a specific class.
     */
    private boolean isTeacherAssignedToClass(UserId teacherId, ClassId classId) {
        Optional<TeacherClassAssignment> assignment =
                teacherClassAssignmentRepository.findByTeacherIdAndClassId(teacherId, classId);
        return assignment.isPresent();
    }

    /**
     * Check if a student is in a class assigned to this teacher.
     */
    private boolean isStudentInTeacherClass(UserId teacherId, StudentId studentId) {
        // Get the student to find their class
        Optional<Student> studentOpt = studentRepository.findById(studentId);
        if (studentOpt.isEmpty()) {
            return false;
        }

        Student student = studentOpt.get();
        UserId studentClassId = student.classId();

        // Get teacher's assigned classes
        List<TeacherClassAssignment> assignments = teacherClassAssignmentRepository.findByTeacherId(teacherId);

        // Check if student's class matches any of the teacher's assigned classes
        for (TeacherClassAssignment assignment : assignments) {
            // Compare the underlying UUID values since Student.classId is UserId
            // and TeacherClassAssignment.classId is ClassId
            if (studentClassId.value().equals(assignment.classId().value())) {
                return true;
            }
        }

        return false;
    }

    /**
     * Check if parent is linked to a specific student.
     */
    private boolean isParentLinkedToStudent(UserId parentId, StudentId studentId) {
        Optional<ParentStudentAssociation> association =
                parentStudentAssociationRepository.findByParentIdAndStudentId(parentId, studentId);
        return association.isPresent();
    }
}
