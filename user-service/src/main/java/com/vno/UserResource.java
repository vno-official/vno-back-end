package com.vno;


import com.vno.user.entity.UserProfile;
import com.vno.user.dto.*;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.jwt.JsonWebToken;

import jakarta.inject.Inject;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.SecurityContext;
import java.util.Optional;

@Path("/api/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource {

    @Inject
    JsonWebToken jwt;

    
    @POST
    @Transactional
    public Response createUser(CreateUserRequest req) {
        if (UserProfile.count("email", req.email()) > 0) {
            return Response.status(400).entity("Email đã tồn tại").build();
        }
        UserProfile user = new UserProfile();
        user.email = req.email();
        user.name = req.name();
        user.phone = req.phone();
        user.persist();
        return Response.status(201).entity(user.id).build();
    }

    
    @GET
    @Path("/test")
    public Response test() {
        return Response.ok("test").build();
    }
    @GET
    @Path("/me")
    public Response getMyInfo(@Context SecurityContext ctx) {
        String email = jwt.getName(); 
        Optional<UserProfile> user = UserProfile.find("email", email).firstResultOptional();
        return user.map(u -> Response.ok(new UserInfoResponse(u.id, u.email, u.name, u.phone)).build())
                   .orElse(Response.status(404).build());
    }

    
    @PUT
    @Path("/me")
    @Transactional
    public Response updateProfile(@Context SecurityContext ctx, UpdateUserRequest req) {
        String email = jwt.getName();
        Optional<UserProfile> userOpt = UserProfile.find("email", email).firstResultOptional();
        if (userOpt.isEmpty()) return Response.status(404).build();
        UserProfile user = userOpt.get();
        user.name = req.name() != null ? req.name() : user.name;
        user.phone = req.phone() != null ? req.phone() : user.phone;
        user.persist();
        return Response.ok().build();
    }

    
    @DELETE
    @Path("/me")
    @Transactional
    public Response archiveMe(@Context SecurityContext ctx) {
        String email = jwt.getName();
        Optional<UserProfile> userOpt = UserProfile.find("email", email).firstResultOptional();
        if (userOpt.isEmpty()) return Response.status(404).build();
        UserProfile user = userOpt.get();
        user.archived = true;
        user.persist();
        return Response.noContent().build();
    }

    
    @GET
    @Path("/verify")
    public Response verifyUser(@QueryParam("email") String email) {
        boolean exists = UserProfile.count("email", email) > 0;
        return Response.ok(exists).build();
    }
}