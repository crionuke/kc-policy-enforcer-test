package com.omgservers.omgservice.errors;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.validation.ValidationException;
import jakarta.ws.rs.core.Response;
import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;

@ApplicationScoped
public class ExceptionMappers {

    @ServerExceptionMapper
    public RestResponse<ErrorResponse> resourceNotFoundMapper(final ResourceNotFound e) {
        final var error = new ErrorResponse();
        error.code = e.getClass().getSimpleName();
        error.message = e.getMessage();
        return RestResponse.status(Response.Status.NOT_FOUND, error);
    }

    @ServerExceptionMapper
    public RestResponse<ErrorResponse> resourceConflictMapper(final ResourceConflict e) {
        final var error = new ErrorResponse();
        error.code = e.getClass().getSimpleName();
        error.message = e.getMessage();
        return RestResponse.status(Response.Status.CONFLICT, error);
    }

    @ServerExceptionMapper
    public RestResponse<ErrorResponse> validationExceptionMapper(final ValidationException e) {
        final var error = new ErrorResponse();
        error.code = "ValidationFailed";
        error.message = e.getMessage();
        return RestResponse.status(Response.Status.BAD_REQUEST, error);
    }
}
