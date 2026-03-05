package com.pug.shared.presenter.rest.mappers;

import com.pug.shared.exceptions.ResourceNotFoundException;
import com.pug.shared.i18n.I18n;
import com.pug.shared.presenter.rest.ApiEnvelope;
import com.pug.shared.presenter.rest.ApiError;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

/**
 * Maps {@link ResourceNotFoundException} to an HTTP 404 (Not Found) response.
 *
 * <p>Provides a clean and standard 404 API error response, supplying the specific domain code
 * (e.g., CITY_NOT_FOUND) to help the client understand exactly what was missing.
 */
@Provider
public class ResourceNotFoundExceptionMapper implements ExceptionMapper<ResourceNotFoundException> {

  @Inject I18n i18n;

  /**
   * Converts a ResourceNotFoundException into a structured HTTP 404 response.
   *
   * @param ex The ResourceNotFoundException representing the missing entity.
   * @return A Response object with status 404 and a JSON body containing the exact error.
   */
  @Override
  public Response toResponse(ResourceNotFoundException ex) {
    String code = ex.getCode().getCode();
    String message = i18n.translation(ex.getCode().getBundleKey());

    ApiError error = ApiError.of(code, message);

    return Response.status(Response.Status.NOT_FOUND)
        .type(MediaType.APPLICATION_JSON_TYPE)
        .entity(ApiEnvelope.error(error))
        .build();
  }
}
