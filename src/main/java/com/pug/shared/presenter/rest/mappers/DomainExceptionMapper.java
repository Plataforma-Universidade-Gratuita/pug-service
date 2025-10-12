package com.pug.shared.presenter.rest.mappers;

import com.pug.shared.domain.exceptions.DomainException;
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
public class DomainExceptionMapper implements ExceptionMapper<DomainException> {

  @Inject I18n i18n;

  @Override
  public Response toResponse(DomainException ex) {
    String code = ex.code();
    String msg = i18n.t(ErrorCodes.bundleKey(code));
    return Response.status(Response.Status.BAD_REQUEST)
        .type(MediaType.APPLICATION_JSON_TYPE)
        .entity(ApiEnvelope.error(code, msg, buildDetails(ex)))
        .build();
  }

  private Map<String, Object> buildDetails(DomainException ex) {
    // to be mapped later
    return Map.of();
  }
}
