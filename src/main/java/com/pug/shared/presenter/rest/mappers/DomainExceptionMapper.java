package com.pug.shared.presenter.rest.mappers;

import com.pug.shared.errors.GenericErrorCodes;
import com.pug.shared.exceptions.DomainException;
import com.pug.shared.i18n.I18n;
import com.pug.shared.presenter.rest.ApiEnvelope;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
@ApplicationScoped
public class DomainExceptionMapper implements ExceptionMapper<DomainException> {

  @Inject I18n i18n;

  @Override
  public Response toResponse(DomainException ex) {
    GenericErrorCodes code = ex.code();
    String msg = i18n.t(code.getBundleKey());
    return Response.status(Response.Status.BAD_REQUEST)
        .type(MediaType.APPLICATION_JSON_TYPE)
        .entity(ApiEnvelope.error(code.toString(), msg, ex.getDetails()))
        .build();
  }
}
