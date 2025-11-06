package com.pug.shared.presenter.rest.mappers;

import com.pug.shared.errors.ErrorCodes;
import com.pug.shared.i18n.I18n;
import com.pug.shared.presenter.rest.ApiEnvelope;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

/** Maps ConstraintViolationException to a standardized API error response. */
@Provider
@ApplicationScoped
public class ConstraintViolationExceptionMapper
    implements ExceptionMapper<ConstraintViolationException> {

  @Inject I18n i18n;

  /**
   * Handles ConstraintViolationException and constructs a detailed error response.
   *
   * @param ex the ConstraintViolationException instance.
   * @return a Response object with error details.
   */
  @Override
  public Response toResponse(ConstraintViolationException ex) {
    var violations =
        ex.getConstraintViolations().stream()
            .map(
                v ->
                    Map.of(
                        "field",
                        v.getPropertyPath() == null ? "" : v.getPropertyPath().toString(),
                        "message",
                        v.getMessage()))
            .collect(Collectors.toList());

    Map<String, Object> details = new LinkedHashMap<>();
    details.put("count", violations.size());
    details.put("violations", violations);

    String msg = i18n.translation(ErrorCodes.VALIDATION_ERROR.getBundleKey());

    return Response.status(422)
        .type(MediaType.APPLICATION_JSON_TYPE)
        .entity(ApiEnvelope.error(ErrorCodes.VALIDATION_ERROR.toString(), msg, details))
        .build();
  }
}
