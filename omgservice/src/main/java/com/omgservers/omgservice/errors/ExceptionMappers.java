package com.omgservers.omgservice.errors;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.validation.ValidationException;
import jakarta.ws.rs.core.Response;
import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class ExceptionMappers {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionMappers.class);

    @ServerExceptionMapper
    public RestResponse<ErrorResponse> badRequestMapper(final BadRequest e) {
        LOGGER.warn("{}", e.getMessage());

        final var error = new ErrorResponse();
        error.code = e.getClass().getSimpleName();
        error.message = e.getMessage();
        return RestResponse.status(Response.Status.BAD_REQUEST, error);
    }

    @ServerExceptionMapper
    public RestResponse<ErrorResponse> resourceNotFoundMapper(final ResourceNotFound e) {
        LOGGER.warn("{}", e.getMessage());

        final var error = new ErrorResponse();
        error.code = e.getClass().getSimpleName();
        error.message = e.getMessage();
        return RestResponse.status(Response.Status.NOT_FOUND, error);
    }

    @ServerExceptionMapper
    public RestResponse<ErrorResponse> resourceConflictMapper(final ResourceConflict e) {
        LOGGER.warn("{}", e.getMessage());

        final var error = new ErrorResponse();
        error.code = e.getClass().getSimpleName();
        error.message = e.getMessage();
        return RestResponse.status(Response.Status.CONFLICT, error);
    }

    @ServerExceptionMapper
    public RestResponse<ErrorResponse> validationExceptionMapper(final ValidationException e) {
        LOGGER.warn("{}", e.getMessage());

        final var error = new ErrorResponse();
        error.code = "ValidationFailed";
        error.message = e.getMessage();
        return RestResponse.status(Response.Status.BAD_REQUEST, error);
    }

    @ServerExceptionMapper
    public RestResponse<ErrorResponse> validationExceptionMapper(final Exception e) {
        LOGGER.error("{}", e.getMessage(), e);

        final var error = new ErrorResponse();
        error.code = "InternalError";
        error.message = e.getMessage();
        return RestResponse.status(Response.Status.INTERNAL_SERVER_ERROR, error);
    }
}
