package com.pug.shared.presenter.rest.mappers;

import com.pug.shared.domain.enums.SharedErrorCodes;
import com.pug.shared.exceptions.DataIntegrityException;
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

/**
 * Maps ReferencedEntityException to an HTTP 409 (Conflict) response. Used when trying to delete an
 * entity that is still being used by others.
 */
@Provider
public class DataIntegrityExceptionMapper implements ExceptionMapper<DataIntegrityException> {

  @Inject I18n i18n;

  /**
   * Converts a ReferencedEntityException into an HTTP 409 response with a localized error message.
   *
   * @param ex the ReferencedEntityException to be mapped.
   * @return a Response with status 409 and error details in the body.
   */
  @Override
  public Response toResponse(DataIntegrityException ex) {
    Details details = new Details(List.of(PresenterUtils.mapProblemsToFieldErrors(ex.getProblem(), i18n)));
    ApiError error = ApiError.of(SharedErrorCodes.DATA_INTEGRITY_ERROR.name(),
            i18n.translation(SharedErrorCodes.DATA_INTEGRITY_ERROR.getBundleKey()), details);

    return Response.status(Response.Status.CONFLICT)
        .type(MediaType.APPLICATION_JSON_TYPE)
        .entity(ApiEnvelope.error(error))
        .build();
  }
}
