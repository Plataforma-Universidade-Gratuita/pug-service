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
 * Maps {@link DuplicateResourceException} to an HTTP 409 (Conflict) response.
 * <p>
 * Secures the application by returning a high-level, localized message indicating a
 * conflict (e.g., "User already exists") without echoing back potentially sensitive
 * conflicting values (like CPFs or emails) in the response body.
 */
@Provider
public class DuplicateResourceExceptionMapper implements ExceptionMapper<DuplicateResourceException> {

  @Inject
  I18n i18n;

  /**
   * Converts a DuplicateResourceException into a structured HTTP 409 response.
   *
   * @param ex The DuplicateResourceException representing the conflict.
   * @return A Response object with status 409 and a JSON body containing the error code.
   */
  @Override
  public Response toResponse(DuplicateResourceException ex) {
    String code = ex.getCode().getCode();
    String message = i18n.translation(ex.getCode().getBundleKey());

    ApiError error = ApiError.of(code, message);

    return Response.status(Response.Status.CONFLICT)
            .type(MediaType.APPLICATION_JSON_TYPE)
            .entity(ApiEnvelope.error(error))
            .build();
  }
}