package com.pug.shared.presenter.rest.mappers;

import com.pug.shared.exceptions.DuplicateResourceException;
import com.pug.shared.i18n.I18n;
import com.pug.shared.presenter.rest.ApiEnvelope;
import com.pug.shared.presenter.rest.ApiError;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

/**
 * Maps DuplicateResourceException to an HTTP 409 (Conflict) response.
 */
@Provider
public class DuplicateResourceExceptionMapper implements ExceptionMapper<DuplicateResourceException> {

    @Inject
    I18n i18n;

    /**
     * Converts a DuplicateResourceException into an HTTP response.
     *
     * @param ex the DuplicateResourceException to convert.
     * @return a Response with status 409 and error details.
     */
    @Override
    public Response toResponse(DuplicateResourceException ex) {
        String msg = i18n.translation(ex.getErrorCode().getBundleKey());

        ApiError error = ApiError.of(
                ex.getErrorCode().toString(),
                msg,
                ex.getDetails()
        );

        return Response.status(Response.Status.CONFLICT)
                .type(MediaType.APPLICATION_JSON_TYPE)
                .entity(ApiEnvelope.error(error))
                .build();
    }
}