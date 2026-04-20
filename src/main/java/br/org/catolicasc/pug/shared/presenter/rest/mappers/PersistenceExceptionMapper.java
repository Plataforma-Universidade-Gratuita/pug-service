package br.org.catolicasc.pug.shared.presenter.rest.mappers;

import br.org.catolicasc.pug.shared.domain.enums.SharedErrorCodes;
import br.org.catolicasc.pug.shared.i18n.I18n;
import br.org.catolicasc.pug.shared.presenter.rest.ApiEnvelope;
import br.org.catolicasc.pug.shared.presenter.rest.ApiError;
import jakarta.inject.Inject;
import jakarta.persistence.PersistenceException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.util.Locale;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.exception.DataException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A highly generic exception mapper for handling {@link PersistenceException} and its underlying
 * causes.
 *
 * <p>Instead of coupling to specific domain tables or fields, this mapper analyzes database
 * constraint naming conventions (e.g., "uq_", "_key", "_fkey") to safely map low-level database
 * errors to standardized {@link SharedErrorCodes}. This acts as a security boundary, ensuring
 * internal database structures are never exposed to the client.
 */
@Provider
public class PersistenceExceptionMapper implements ExceptionMapper<PersistenceException> {

  private static final Logger LOG = LoggerFactory.getLogger(PersistenceExceptionMapper.class);

  @Inject I18n i18n;

  /**
   * Maps a generic PersistenceException to an appropriate HTTP response.
   *
   * @param ex The PersistenceException thrown during database operations.
   * @return A secure, standardized API response.
   */
  @Override
  public Response toResponse(PersistenceException ex) {
    Throwable cause = ex.getCause();
    if (cause instanceof ConstraintViolationException cve) {
      return handleConstraintViolation(cve.getConstraintName(), cve);
    }
    if (cause instanceof DataException) {
      LOG.error("Data exception occurred (truncation or invalid format): ", ex);
      return buildResponse(SharedErrorCodes.DATA_INTEGRITY_ERROR, Response.Status.CONFLICT);
    }
    LOG.error("Generic persistence error intercepted: ", ex);
    return buildResponse(SharedErrorCodes.INTERNAL_ERROR, Response.Status.INTERNAL_SERVER_ERROR);
  }

  /**
   * Evaluates the constraint name generically to determine if it is a unique constraint or a
   * foreign key integrity violation.
   *
   * @param constraintName The raw database constraint name.
   * @param ex The original exception for internal logging.
   * @return A safe HTTP 409 Conflict response.
   */
  private Response handleConstraintViolation(
      String constraintName, ConstraintViolationException ex) {
    if (constraintName == null) {
      LOG.warn("Database constraint violation with null constraint name", ex);
      return buildResponse(SharedErrorCodes.DATA_INTEGRITY_ERROR, Response.Status.CONFLICT);
    }
    String lowerName = constraintName.toLowerCase(Locale.ROOT);
    if (lowerName.contains("uq_") || lowerName.endsWith("_key") || lowerName.contains("unique")) {
      LOG.info("Unique constraint violation intercepted: {}", constraintName);
      return buildResponse(SharedErrorCodes.DUPLICATED_RESOURCE_ERROR, Response.Status.CONFLICT);
    }
    if (lowerName.contains("fk_") || lowerName.endsWith("_fkey")) {
      LOG.info("Foreign key constraint violation intercepted: {}", constraintName);
      return buildResponse(SharedErrorCodes.DATA_INTEGRITY_ERROR, Response.Status.CONFLICT);
    }
    LOG.warn("Unknown database constraint violation: {}", constraintName, ex);
    return buildResponse(SharedErrorCodes.DATA_INTEGRITY_ERROR, Response.Status.CONFLICT);
  }

  /**
   * Helper method to construct the final secure API error response.
   *
   * @param code The shared generic error code.
   * @param status The HTTP status to return.
   * @return The final Response object.
   */
  private Response buildResponse(SharedErrorCodes code, Response.Status status) {
    String msg = i18n.translation(code.getBundleKey());
    ApiError error = ApiError.of(code.getCode(), msg);

    return Response.status(status)
        .type(MediaType.APPLICATION_JSON_TYPE)
        .entity(ApiEnvelope.error(error))
        .build();
  }
}
