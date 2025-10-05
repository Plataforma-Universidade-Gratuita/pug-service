package com.pug.shared.mappers;

import com.pug.shared.dtos.ApiError;
import com.pug.shared.dtos.ApiResponse;
import com.pug.shared.errors.DomainException;
import com.pug.shared.errors.ErrorCodes;
import com.pug.shared.i18n.I18n;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.util.Map;

@Provider
public class DomainExceptionMapper implements ExceptionMapper<DomainException> {
  @Context HttpHeaders headers;
  @Inject I18n i18n;

  private static final Map<String, Response.Status> STATUS =
      Map.of(
          ErrorCodes.USER_DUPLICATE_CPF, Response.Status.CONFLICT,
          ErrorCodes.USER_NOT_FOUND, Response.Status.NOT_FOUND);

  @Override
  public Response toResponse(DomainException ex) {
    var loc = i18n.resolve(headers);
    var code = ex.getCode();
    var msg = i18n.msg(ErrorCodes.bundleKey(code), loc, ex.getArgs());
    var details = buildDetails(ex);

    var body = ApiResponse.error(ApiError.of(code, msg, details));

    return Response.status(STATUS.getOrDefault(code, Response.Status.BAD_REQUEST))
        .entity(body)
        .type(MediaType.APPLICATION_JSON)
        .build();
  }

  private Map<String, Object> buildDetails(DomainException ex) {
    if (ErrorCodes.USER_NOT_FOUND.equals(ex.getCode()) && ex.getArgs().length == 1) {
      Object a = ex.getArgs()[0];
      return (a instanceof java.util.UUID) ? Map.of("id", a) : Map.of("cpf", a);
    }
    return Map.of();
  }
}
