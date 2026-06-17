package br.org.catolicasc.pug.shared.presenter.rest.mappers;

import br.org.catolicasc.pug.shared.domain.enums.SharedErrorCodes;
import br.org.catolicasc.pug.shared.i18n.I18n;
import br.org.catolicasc.pug.shared.presenter.rest.ApiEnvelope;
import br.org.catolicasc.pug.shared.presenter.rest.ApiError;
import br.org.catolicasc.pug.shared.presenter.rest.Details;
import br.org.catolicasc.pug.shared.presenter.rest.FieldErrorsResponse;
import br.org.catolicasc.pug.shared.utils.PresenterUtils;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.util.List;
import java.util.stream.Collectors;
import org.jboss.logging.Logger;

/**
 * Mapper to catch {@link jakarta.validation.ConstraintViolationException} (Bean Validation) and
 * return a detailed HTTP 422 response.
 *
 * <p>This intercepts errors thrown by standard validation annotations (e.g., {@code @NotNull},
 * {@code @Size}) before they reach the domain layer, grouping multiple violations by their
 * respective field names.
 */
@Provider
public class ConstraintViolationExceptionMapper
    implements ExceptionMapper<ConstraintViolationException> {

  private static final Logger LOG = Logger.getLogger(ConstraintViolationExceptionMapper.class);

  @Inject I18n i18n;
  @Context HttpHeaders headers;

  /**
   * Converts a ConstraintViolationException into an HTTP 422 Unprocessable Entity response.
   *
   * <p>The response body contains a structured JSON with error details, grouping specific field
   * errors extracted from the constraint violations so the client can fix all issues at once.
   *
   * @param ex The ConstraintViolationException thrown during request payload validation.
   * @return A Response object with status 422 and a JSON body containing grouped error details.
   */
  /** {@inheritDoc} */
  @Override
  public Response toResponse(ConstraintViolationException ex) {
    LOG.debugf(ex, "Bean validation constraint violation");
    List<FieldErrorsResponse> groupedViolations =
        ex.getConstraintViolations().stream()
            .collect(
                Collectors.groupingBy(
                    this::extractFieldName,
                    Collectors.mapping(this::mapToDetail, Collectors.toList())))
            .entrySet()
            .stream()
            .map(entry -> new FieldErrorsResponse(entry.getKey(), entry.getValue()))
            .toList();

    String msg =
        i18n.translation(
            SharedErrorCodes.VALIDATION_ERROR.getBundleKey(), PresenterUtils.pickLocale(headers));
    ApiError error =
        ApiError.of(
            SharedErrorCodes.VALIDATION_ERROR.getCode(), msg, new Details(groupedViolations));
    return Response.status(422)
        .type(MediaType.APPLICATION_JSON_TYPE)
        .entity(ApiEnvelope.error(error))
        .build();
  }

  private String extractFieldName(ConstraintViolation<?> violation) {
    return violation.getPropertyPath() == null ? "unknown" : violation.getPropertyPath().toString();
  }

  private FieldErrorsResponse.FieldErrorDetail mapToDetail(ConstraintViolation<?> violation) {
    String code = SharedErrorCodes.VALIDATION_ERROR.getCode();
    if (violation.getConstraintDescriptor() != null
        && violation.getConstraintDescriptor().getAnnotation() != null) {
      code = violation.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName();
    }
    String message = violation.getMessage();
    return new FieldErrorsResponse.FieldErrorDetail(code, message);
  }
}
