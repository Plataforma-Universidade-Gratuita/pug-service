package com.pug.shared.presenter.rest.mappers;

import com.pug.shared.domain.enums.SharedErrorCodes;
import com.pug.shared.exceptions.ResourceNotFoundException;
import com.pug.shared.i18n.I18n;
import com.pug.shared.presenter.rest.ApiEnvelope;
import com.pug.shared.presenter.rest.ApiError;
import com.pug.shared.presenter.rest.Details;
import com.pug.shared.utils.PresenterUtils;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.util.List;

/** Maps ResourceNotFoundException to an HTTP 404 response. */
@Provider
public class ResourceNotFoundExceptionMapper implements ExceptionMapper<ResourceNotFoundException> {

  @Inject I18n i18n;

  /**
   * Maps ResourceNotFoundException to an HTTP 404 response with error details.
   *
   * @param ex The caught exception.
   * @return An HTTP 404 response with error details.
   */
  @Override
  public Response toResponse(ResourceNotFoundException ex) {
    Details details = new Details(List.of(PresenterUtils.mapProblemsToFieldErrors(ex.getProblem(), i18n)));
    ApiError error = ApiError.of(SharedErrorCodes.RESOURCE_NOT_FOUND_ERROR.name(),
            i18n.translation(SharedErrorCodes.RESOURCE_NOT_FOUND_ERROR.getBundleKey()), details);

    return Response.status(Response.Status.NOT_FOUND)
        .type(MediaType.APPLICATION_JSON_TYPE)
        .entity(ApiEnvelope.error(error))
        .build();
  }
}
