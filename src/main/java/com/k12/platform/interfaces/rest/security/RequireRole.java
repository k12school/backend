package com.k12.platform.interfaces.rest.security;

import jakarta.interceptor.InterceptorBinding;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to specify required roles for endpoint access.
 * Use on REST endpoint methods to enforce role-based access control.
 *
 * Example:
 * <pre>
 * {@code
 * @GET
 * @Path("/students/{id}")
 * @RequireRole({UserRole.ADMIN, UserRole.TEACHER})
 * public Response getStudent(@PathParam("id") String id) {
 *     // Only admins and teachers can access
 * }
 * }
 * </pre>
 */
@InterceptorBinding
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface RequireRole {

    /**
     * Roles that are allowed to access the annotated endpoint.
     * If empty, any authenticated user can access.
     */
    UserRole[] value() default {};

    /**
     * If true, user must have ALL specified roles.
     * If false, user must have AT LEAST ONE of the specified roles.
     */
    boolean requireAll() default false;
}
