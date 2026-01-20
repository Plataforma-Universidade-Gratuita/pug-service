package com.pug.shared.presenter.rest.mappers;

import com.pug.shared.domain.enums.SharedErrorCodes;
import com.pug.shared.i18n.I18n;
import com.pug.shared.presenter.rest.ApiEnvelope;
import com.pug.shared.presenter.rest.ApiError;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Mapper to catch uncaught exceptions and return a generic error response.
 */
@Provider
public class UncaughtExceptionMapper implements ExceptionMapper<Throwable> {

  private static final Logger LOG = LoggerFactory.getLogger(UncaughtExceptionMapper.class);

  @Inject
  I18n i18n;

  /**
   * Maps uncaught exceptions to an HTTP 500 response with a generic error message.
   *
   * @param ex The caught exception.
   * @return An HTTP 500 response with error details.
   */
  @Override
  public Response toResponse(Throwable ex) {
    LOG.error("An uncaught exception occurred:", ex);

    String msg = i18n.translation(SharedErrorCodes.INTERNAL_ERROR.getBundleKey());

    ApiError apiError = ApiError.of(
            SharedErrorCodes.INTERNAL_ERROR.name(),
            msg,
            Map.of("exceptionType", ex.getClass().getSimpleName())
    );

    return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
            .type(MediaType.APPLICATION_JSON_TYPE)
            .entity(ApiEnvelope.error(apiError))
            .build();
  }
}