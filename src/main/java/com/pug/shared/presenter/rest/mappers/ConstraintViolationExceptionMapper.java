package com.pug.shared.presenter.rest.mappers;

import com.pug.shared.domain.enums.SharedErrorCodes;
import com.pug.shared.i18n.I18n;
import com.pug.shared.presenter.rest.ApiEnvelope;
import com.pug.shared.presenter.rest.ApiError;
import com.pug.shared.presenter.rest.Details;
import com.pug.shared.presenter.rest.FieldError;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.util.List;

/**
 * Mapper to catch ConstraintViolationException and return a detailed error response.
 */
@Provider
public class ConstraintViolationExceptionMapper
        implements ExceptionMapper<ConstraintViolationException> {

  @Inject
  I18n i18n;

  /**
   * Maps ConstraintViolationException to an HTTP 422 response with violation details.
   *
   * @param ex The caught exception.
   * @return An HTTP 422 response with violation details.
   */
  @Override
  public Response toResponse(ConstraintViolationException ex) {
    List<FieldError> violations =
            ex.getConstraintViolations().stream()
                    .map(this::mapViolationToFieldError).toList();

    String msg = i18n.translation(SharedErrorCodes.VALIDATION_ERROR.getBundleKey());
    ApiError error = new ApiError(SharedErrorCodes.VALIDATION_ERROR.toString(), msg, new Details(violations));

    return Response.status(422)
            .type(MediaType.APPLICATION_JSON_TYPE)
            .entity(ApiEnvelope.error(error))
            .build();
  }

  /**
   * Converts a ConstraintViolation into a FieldError for API response.
   */
  private FieldError mapViolationToFieldError(ConstraintViolation<?> violation) {
    String field = violation.getPropertyPath() == null ? "" : violation.getPropertyPath().toString();
    String message = violation.getMessage();
    return new FieldError(field, null, message);
  }
}
