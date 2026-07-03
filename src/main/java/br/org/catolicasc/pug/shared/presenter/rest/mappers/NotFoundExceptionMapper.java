/*
 * Copyright (c) 2026 Mateus Fernandes and Plataforma Universidade Gratuita.
 * All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized use,
 * copying, modification, distribution, or deployment is prohibited.
 */

package br.org.catolicasc.pug.shared.presenter.rest.mappers;

import br.org.catolicasc.pug.shared.domain.enums.SharedErrorCodes;
import br.org.catolicasc.pug.shared.i18n.I18n;
import br.org.catolicasc.pug.shared.presenter.rest.ApiEnvelope;
import br.org.catolicasc.pug.shared.presenter.rest.ApiError;
import br.org.catolicasc.pug.shared.utils.PresenterUtils;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.jboss.logging.Logger;

/**
 * Maps framework-level {@link NotFoundException} responses.
 *
 * <p>Resteasy Reactive wraps malformed parameter conversions such as invalid UUID query params as
 * {@code NotFoundException}. Those cases should be reported as validation failures (400), while
 * genuine route/resource misses should stay as 404.
 */
@Provider
public class NotFoundExceptionMapper implements ExceptionMapper<NotFoundException> {

  private static final Logger LOG = Logger.getLogger(NotFoundExceptionMapper.class);

  @Inject I18n i18n;
  @Context HttpHeaders headers;

  @Override
  public Response toResponse(NotFoundException exception) {
    if (hasCause(exception, IllegalArgumentException.class)) {
      LOG.debugf(exception, "Malformed request parameter caught as not found");
      return buildResponse(Response.Status.BAD_REQUEST, SharedErrorCodes.VALIDATION_ERROR);
    }

    LOG.debugf(exception, "Framework not found");
    return buildResponse(Response.Status.NOT_FOUND, SharedErrorCodes.RESOURCE_NOT_FOUND_ERROR);
  }

  private Response buildResponse(Response.Status status, SharedErrorCodes code) {
    String message = i18n.translation(code.getBundleKey(), PresenterUtils.pickLocale(headers));
    ApiError apiError = ApiError.of(code.getCode(), message);

    return Response.status(status)
        .type(MediaType.APPLICATION_JSON_TYPE)
        .entity(ApiEnvelope.error(apiError))
        .build();
  }

  private boolean hasCause(Throwable throwable, Class<? extends Throwable> type) {
    Throwable current = throwable;
    while (current != null) {
      if (type.isInstance(current)) {
        return true;
      }
      current = current.getCause();
    }
    return false;
  }
}
