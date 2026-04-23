package br.org.catolicasc.pug.shared.presenter.rest.mappers;

import br.org.catolicasc.pug.shared.domain.enums.SharedErrorCodes;
import br.org.catolicasc.pug.shared.i18n.I18n;
import br.org.catolicasc.pug.shared.presenter.rest.ApiEnvelope;
import br.org.catolicasc.pug.shared.presenter.rest.ApiError;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotAuthorizedException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.jboss.logging.Logger;

/**
 * Maps {@link NotAuthorizedException} to an HTTP 401 (Unauthorized) response.
 *
 * <p>Returns a safe, localized generic error message to prevent credential enumeration attacks
 * (hiding whether the email was wrong or the password was wrong).
 */
@Provider
public class NotAuthorizedExceptionMapper implements ExceptionMapper<NotAuthorizedException> {

  private static final Logger LOG = Logger.getLogger(NotAuthorizedExceptionMapper.class);

  @Inject I18n i18n;

  @Override
  public Response toResponse(NotAuthorizedException ex) {
    LOG.debugf(ex, "Unauthorized access attempt");
    String code = SharedErrorCodes.UNAUTHORIZED_ERROR.getCode();
    String message = i18n.translation(SharedErrorCodes.UNAUTHORIZED_ERROR.getBundleKey());

    ApiError error = ApiError.of(code, message);

    return Response.status(Response.Status.UNAUTHORIZED)
        .type(MediaType.APPLICATION_JSON_TYPE)
        .entity(ApiEnvelope.error(error))
        .build();
  }
}
