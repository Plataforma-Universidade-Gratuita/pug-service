package com.pug.shared.mappers;

import com.pug.shared.dtos.ApiError;
import com.pug.shared.dtos.ApiResponse;
import com.pug.shared.errors.ErrorCodes;
import com.pug.shared.i18n.I18n;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class BeanValidationMapper implements ExceptionMapper<ConstraintViolationException> {
  @Context HttpHeaders headers;
  @Inject I18n i18n;

  @Override
  public Response toResponse(ConstraintViolationException ex) {
    var loc = i18n.resolve(headers);
    var msg = i18n.msg(ErrorCodes.bundleKey(ErrorCodes.VALIDATION_ERROR), loc);
    var details =
        ex.getConstraintViolations().stream()
            .map(
                v ->
                    java.util.Map.of(
                        "field", v.getPropertyPath().toString(), "message", v.getMessage()))
            .toList();
    var body =
        ApiResponse.error(
            ApiError.of(ErrorCodes.VALIDATION_ERROR, msg, java.util.Map.of("violations", details)));
    return Response.status(422).entity(body).type(MediaType.APPLICATION_JSON).build();
  }
}
