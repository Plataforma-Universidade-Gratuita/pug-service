package br.org.catolicasc.pug.shared.presenter.rest.mappers;

import br.org.catolicasc.pug.shared.domain.enums.SharedErrorCodes;
import br.org.catolicasc.pug.shared.i18n.I18n;
import br.org.catolicasc.pug.shared.presenter.rest.ApiEnvelope;
import br.org.catolicasc.pug.shared.presenter.rest.ApiError;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Mapper to catch unexpected or unhandled exceptions and return a secure, generic 500 response. */
@Provider
public class UncaughtExceptionMapper implements ExceptionMapper<Throwable> {

  private static final Logger LOG = LoggerFactory.getLogger(UncaughtExceptionMapper.class);

  @Inject I18n i18n;

  /**
   * Converts an uncaught exception into a standardized HTTP 500 error response.
   *
   * <p>Logs the full stack trace for internal debugging but strictly returns a generic, localized
   * error message to the client. This prevents information disclosure vulnerabilities by ensuring
   * that internal class names, SQL statements, or underlying error messages are never exposed in
   * the API response.
   *
   * @param ex The unexpected exception that was thrown.
   * @return A Response object containing a safe error code and HTTP 500 status.
   */
  @Override
  public Response toResponse(Throwable ex) {
    LOG.error("An uncaught exception occurred:", ex);

    String msg = i18n.translation(SharedErrorCodes.INTERNAL_ERROR.getBundleKey());
    ApiError apiError = ApiError.of(SharedErrorCodes.INTERNAL_ERROR.getCode(), msg);

    return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
        .type(MediaType.APPLICATION_JSON_TYPE)
        .entity(ApiEnvelope.error(apiError))
        .build();
  }
}
