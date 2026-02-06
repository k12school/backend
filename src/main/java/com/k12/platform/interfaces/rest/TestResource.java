package com.k12.platform.interfaces.rest;

import com.k12.platform.domain.port.UserRepository;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

/**
 * Simple test endpoint to verify database connectivity.
 */
@Path("/api/test")
public class TestResource {

    @Inject
    UserRepository userRepository;

    @GET
    @Path("/db")
    public Response testDb() {
        try {
            long count = userRepository.findAll().size();
            return Response.ok("User count: " + count).build();
        } catch (Exception e) {
            return Response.serverError().entity("Error: " + e.getMessage()).build();
        }
    }
}
