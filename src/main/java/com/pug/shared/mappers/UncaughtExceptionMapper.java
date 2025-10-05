package com.pug.shared.mappers;

import com.pug.shared.dtos.ApiError;
import com.pug.shared.dtos.ApiResponse;
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
public class UncaughtExceptionMapper implements ExceptionMapper<Throwable> {
  @Context HttpHeaders headers;
  @Inject I18n i18n;

  @Override
  public Response toResponse(Throwable ex) {
    var loc = i18n.resolve(headers);
    var msg = i18n.msg(ErrorCodes.bundleKey(ErrorCodes.INTERNAL_ERROR), loc);
    var body = ApiResponse.error(ApiError.of(ErrorCodes.INTERNAL_ERROR, msg, Map.of()));
    return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
        .entity(body)
        .type(MediaType.APPLICATION_JSON)
        .build();
  }
}
