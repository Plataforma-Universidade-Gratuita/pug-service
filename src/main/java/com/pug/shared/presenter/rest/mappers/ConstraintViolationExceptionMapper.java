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
 * Mapper to catch jakarta.validation.ConstraintViolationException (Bean Validation) and return a
 * detailed HTTP 422 response.
 */
@Provider
public class ConstraintViolationExceptionMapper
    implements ExceptionMapper<ConstraintViolationException> {

  @Inject I18n i18n;

  /**
   * Converts a ConstraintViolationException into an HTTP 422 Unprocessable Entity response.
   *
   * <p>The response body contains a structured JSON with error details, including the specific
   * field errors extracted from the constraint violations.
   *
   * @param ex The ConstraintViolationException thrown during validation.
   * @return A Response object with status 422 and a JSON body containing error details.
   */
  @Override
  public Response toResponse(ConstraintViolationException ex) {
    List<FieldError> violations =
        ex.getConstraintViolations().stream().map(this::mapViolationToFieldError).toList();
    String msg = i18n.translation(SharedErrorCodes.VALIDATION_ERROR.getBundleKey());

    ApiError error =
        new ApiError(SharedErrorCodes.VALIDATION_ERROR.name(), msg, new Details(violations));

    return Response.status(422)
        .type(MediaType.APPLICATION_JSON_TYPE)
        .entity(ApiEnvelope.error(error))
        .build();
  }

  private FieldError mapViolationToFieldError(ConstraintViolation<?> violation) {
    String field =
        violation.getPropertyPath() == null ? "" : violation.getPropertyPath().toString();
    String message = violation.getMessage();
    return new FieldError(field, null, message);
  }
}
