package com.pug.shared.presenter.rest.mappers;

import com.pug.shared.domain.Problem;
import com.pug.shared.domain.enums.SharedErrorCodes;
import com.pug.shared.exceptions.AppValidationException;
import com.pug.shared.i18n.I18n;
import com.pug.shared.presenter.rest.ApiEnvelope;
import com.pug.shared.presenter.rest.ApiError;
import com.pug.shared.presenter.rest.Details;
import com.pug.shared.presenter.rest.FieldError;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

/** Mapper to catch AppValidationException and return a detailed error response. */
@Provider
public class AppValidationExceptionMapper implements ExceptionMapper<AppValidationException> {

  @Inject I18n i18n;

  /**
   * Maps AppValidationException to an HTTP 400 response with validation error details.
   *
   * @param exception The caught exception.
   * @return An HTTP 400 response with validation error details.
   */
  @Override
  public Response toResponse(AppValidationException exception) {
    Details detailsMap =
        new Details(exception.getProblems().stream().map(this::mapProblemsToFieldErrors).toList());
    ApiError apiError =
        ApiError.of(
            SharedErrorCodes.VALIDATION_ERROR.name(),
            i18n.translation(SharedErrorCodes.VALIDATION_ERROR.getBundleKey()),
            detailsMap);

    return Response.status(Response.Status.BAD_REQUEST).entity(ApiEnvelope.error(apiError)).build();
  }

  /** Helper method to convert a Problem into a FieldError. */
  private FieldError mapProblemsToFieldErrors(Problem problems) {
    String fieldName = problems.getFinalFieldName();
    String errorCode = problems.getErrorCode();
    String message = i18n.translation(problems.getMessageKey());
    return new FieldError(fieldName, errorCode, message);
  }
}
