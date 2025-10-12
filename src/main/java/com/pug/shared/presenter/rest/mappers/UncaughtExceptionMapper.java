package com.pug.shared.presenter.rest.mappers;

import com.pug.shared.errors.ErrorCodes;
import com.pug.shared.i18n.I18n;
import com.pug.shared.presenter.rest.ApiEnvelope;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.util.Map;

@Provider
@ApplicationScoped
public class UncaughtExceptionMapper implements ExceptionMapper<Throwable> {

  @Inject I18n i18n;

  @Override
  public Response toResponse(Throwable ex) {
    String msg = i18n.t(ErrorCodes.bundleKey(ErrorCodes.INTERNAL_ERROR));
    return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
        .type(MediaType.APPLICATION_JSON_TYPE)
        .entity(ApiEnvelope.error(ErrorCodes.INTERNAL_ERROR, msg, Map.of()))
        .build();
  }
}
