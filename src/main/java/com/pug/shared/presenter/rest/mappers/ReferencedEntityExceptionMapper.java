package com.pug.shared.presenter.rest.mappers;

import com.pug.shared.exceptions.ReferencedEntityException;
import com.pug.shared.i18n.I18n;
import com.pug.shared.presenter.rest.ApiEnvelope;
import com.pug.shared.presenter.rest.ApiError;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

/**
 * Maps ReferencedEntityException to an HTTP 409 (Conflict) response. Used when trying to delete an
 * entity that is still being used by others.
 */
@Provider
public class ReferencedEntityExceptionMapper implements ExceptionMapper<ReferencedEntityException> {

  @Inject I18n i18n;

  /**
   * Converts a ReferencedEntityException into an HTTP 409 response with a localized error message.
   *
   * @param ex the ReferencedEntityException to be mapped.
   * @return a Response with status 409 and error details in the body.
   */
  @Override
  public Response toResponse(ReferencedEntityException ex) {
    String msg = i18n.translation(ex.getErrorCode().getBundleKey());

    ApiError error = ApiError.of(ex.getErrorCode().toString(), msg, ex.getDetails());

    return Response.status(Response.Status.CONFLICT)
        .type(MediaType.APPLICATION_JSON_TYPE)
        .entity(ApiEnvelope.error(error))
        .build();
  }
}
