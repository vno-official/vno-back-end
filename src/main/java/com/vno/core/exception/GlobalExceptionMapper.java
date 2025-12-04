package com.vno.core.exception;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.time.Instant;

@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Exception> {

    @Override
    public Response toResponse(Exception exception) {
        ErrorResponse error;
        Response.Status status;

        if (exception instanceof jakarta.ws.rs.ForbiddenException) {
            status = Response.Status.FORBIDDEN;
            error = new ErrorResponse("FORBIDDEN", exception.getMessage(), Instant.now());
        } else if (exception instanceof jakarta.ws.rs.NotAuthorizedException) {
            status = Response.Status.UNAUTHORIZED;
            error = new ErrorResponse("UNAUTHORIZED", exception.getMessage(), Instant.now());
        } else if (exception instanceof jakarta.ws.rs.NotFoundException) {
            status = Response.Status.NOT_FOUND;
            error = new ErrorResponse("NOT_FOUND", exception.getMessage(), Instant.now());
        } else if (exception instanceof jakarta.ws.rs.BadRequestException) {
            status = Response.Status.BAD_REQUEST;
            error = new ErrorResponse("BAD_REQUEST", exception.getMessage(), Instant.now());
        } else if (exception instanceof jakarta.validation.ValidationException) {
            status = Response.Status.BAD_REQUEST;
            error = new ErrorResponse("VALIDATION_ERROR", exception.getMessage(), Instant.now());
        } else {
            // Internal server error for unexpected exceptions
            status = Response.Status.INTERNAL_SERVER_ERROR;
            error = new ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred", Instant.now());
            // Log the exception for debugging
            exception.printStackTrace();
        }

        return Response.status(status)
                .entity(error)
                .build();
    }

    public static class ErrorResponse {
        public String error;
        public String message;
        public Instant timestamp;

        public ErrorResponse(String error, String message, Instant timestamp) {
            this.error = error;
            this.message = message;
            this.timestamp = timestamp;
        }
    }
}
