package com.pug.shared.presenter.rest.mappers;

import com.pug.shared.domain.enums.SharedErrorCodes;
import com.pug.shared.exceptions.DuplicateResourceException;
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

/** Maps DuplicateResourceException to an HTTP 409 (Conflict) response. */
@Provider
public class DuplicateResourceExceptionMapper
    implements ExceptionMapper<DuplicateResourceException> {

  @Inject I18n i18n;

  /**
   * Converts a DuplicateResourceException into an HTTP response.
   *
   * @param ex the DuplicateResourceException to convert.
   * @return a Response with status 409 and error details.
   */
  @Override
  public Response toResponse(DuplicateResourceException ex) {
    Details details = new Details(List.of(PresenterUtils.mapProblemsToFieldErrors(ex.getProblem(), i18n)));
    ApiError error = ApiError.of(SharedErrorCodes.DUPLICATED_RESOURCE_ERROR.name(),
            i18n.translation(SharedErrorCodes.DUPLICATED_RESOURCE_ERROR.getBundleKey()), details);

    return Response.status(Response.Status.CONFLICT)
        .type(MediaType.APPLICATION_JSON_TYPE)
        .entity(ApiEnvelope.error(error))
        .build();
  }
}
