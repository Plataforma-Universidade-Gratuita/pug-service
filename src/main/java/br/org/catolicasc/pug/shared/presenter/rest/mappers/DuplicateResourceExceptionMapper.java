/*
 * Copyright (c) 2026 Mateus Fernandes and Plataforma Universidade Gratuita.
 * All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized use,
 * copying, modification, distribution, or deployment is prohibited.
 */

package br.org.catolicasc.pug.shared.presenter.rest.mappers;

import br.org.catolicasc.pug.shared.exceptions.DuplicateResourceException;
import br.org.catolicasc.pug.shared.i18n.I18n;
import br.org.catolicasc.pug.shared.presenter.rest.ApiEnvelope;
import br.org.catolicasc.pug.shared.presenter.rest.ApiError;
import br.org.catolicasc.pug.shared.utils.PresenterUtils;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.jboss.logging.Logger;

/**
 * Maps {@link DuplicateResourceException} to an HTTP 409 (Conflict) response.
 *
 * <p>Secures the application by returning a high-level, localized message indicating a conflict
 * (e.g., "User already exists") without echoing back potentially sensitive conflicting values (like
 * CPFs or emails) in the response body.
 */
@Provider
public class DuplicateResourceExceptionMapper
    implements ExceptionMapper<DuplicateResourceException> {

  private static final Logger LOG = Logger.getLogger(DuplicateResourceExceptionMapper.class);

  @Inject I18n i18n;
  @Context HttpHeaders headers;

  /**
   * Converts a DuplicateResourceException into a structured HTTP 409 response.
   *
   * @param ex The DuplicateResourceException representing the conflict.
   * @return A Response object with status 409 and a JSON body containing the error code.
   */
  @Override
  public Response toResponse(DuplicateResourceException ex) {
    LOG.debugf(ex, "Duplicate resource conflict");
    String code = ex.getCode().getCode();
    String message =
        i18n.translation(ex.getCode().getBundleKey(), PresenterUtils.pickLocale(headers));

    ApiError error = ApiError.of(code, message);

    return Response.status(Response.Status.CONFLICT)
        .type(MediaType.APPLICATION_JSON_TYPE)
        .entity(ApiEnvelope.error(error))
        .build();
  }
}
