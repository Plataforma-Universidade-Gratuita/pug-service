package com.pug.shared.presenter.rest.mappers;

import com.pug.shared.domain.enums.GenericErrorCodes;
import com.pug.shared.exceptions.DomainException;
import com.pug.shared.i18n.I18n;
import com.pug.shared.presenter.rest.ApiEnvelope;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

/** Maps DomainException to HTTP responses. */
@Provider
@ApplicationScoped
public class DomainExceptionMapper implements ExceptionMapper<DomainException> {

  @Inject I18n i18n;

  /**
   * Converts DomainException to a HTTP Response.
   *
   * @param ex the DomainException to be converted.
   * @return the corresponding HTTP Response.
   */
  @Override
  public Response toResponse(DomainException ex) {
    GenericErrorCodes code = ex.code();
    String msg = i18n.translation(code.getBundleKey());
    return Response.status(Response.Status.BAD_REQUEST)
        .type(MediaType.APPLICATION_JSON_TYPE)
        .entity(ApiEnvelope.error(code.toString(), msg, ex.getDetails()))
        .build();
  }
}
