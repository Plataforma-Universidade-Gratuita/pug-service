package com.pug.shared.presenter.rest.mappers;

import com.pug.shared.domain.enums.ErrorCodes;
import com.pug.shared.i18n.I18n;
import com.pug.shared.presenter.rest.ApiEnvelope;
import com.pug.shared.presenter.rest.ApiError;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.util.LinkedHashMap;
import java.util.Map;

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
    var violations = ex.getConstraintViolations().stream().map(v -> Map.of(
            "field", v.getPropertyPath() == null ? "" : v.getPropertyPath().toString(),
            "message", v.getMessage())).toList();

    Map<String, Object> details = new LinkedHashMap<>();
    details.put("count", violations.size());
    details.put("violations", violations);

    String msg = i18n.translation(ErrorCodes.VALIDATION_ERROR.getBundleKey());
    ApiError error = new ApiError(ErrorCodes.VALIDATION_ERROR.toString(), msg, details);

    return Response.status(422)
            .type(MediaType.APPLICATION_JSON_TYPE)
            .entity(ApiEnvelope.error(error))
            .build();
  }
}