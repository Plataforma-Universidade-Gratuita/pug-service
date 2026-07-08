package br.org.catolicasc.pug.shared.presenter.rest.mappers;

import br.org.catolicasc.pug.shared.domain.enums.SharedErrorCodes;
import br.org.catolicasc.pug.shared.i18n.I18n;
import br.org.catolicasc.pug.shared.presenter.rest.ApiEnvelope;
import br.org.catolicasc.pug.shared.presenter.rest.ApiError;
import br.org.catolicasc.pug.shared.utils.PresenterUtils;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.jboss.logging.Logger;

/** Mapper to catch malformed request parameters and return a standardized HTTP 400 response. */
@Provider
public class BadRequestExceptionMapper implements ExceptionMapper<BadRequestException> {

  private static final Logger LOG = Logger.getLogger(BadRequestExceptionMapper.class);

  @Inject I18n i18n;
  @Context HttpHeaders headers;

  @Override
  public Response toResponse(BadRequestException exception) {
    LOG.debugf(exception, "Bad request caught");

    String msg =
        i18n.translation(
            SharedErrorCodes.VALIDATION_ERROR.getBundleKey(), PresenterUtils.pickLocale(headers));
    ApiError apiError = ApiError.of(SharedErrorCodes.VALIDATION_ERROR.getCode(), msg);

    return Response.status(Response.Status.BAD_REQUEST)
        .type(MediaType.APPLICATION_JSON_TYPE)
        .entity(ApiEnvelope.error(apiError))
        .build();
  }
}
