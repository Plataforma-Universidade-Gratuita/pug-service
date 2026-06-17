package br.org.catolicasc.pug.shared.presenter.rest.mappers;

import br.org.catolicasc.pug.shared.domain.enums.GenericFieldErrorCodes;
import br.org.catolicasc.pug.shared.domain.enums.SharedErrorCodes;
import br.org.catolicasc.pug.shared.exceptions.AppValidationException;
import br.org.catolicasc.pug.shared.i18n.I18n;
import br.org.catolicasc.pug.shared.presenter.rest.ApiEnvelope;
import br.org.catolicasc.pug.shared.presenter.rest.ApiError;
import br.org.catolicasc.pug.shared.presenter.rest.Details;
import br.org.catolicasc.pug.shared.presenter.rest.FieldErrorsResponse;
import br.org.catolicasc.pug.shared.utils.CollectionUtils;
import br.org.catolicasc.pug.shared.utils.PresenterUtils;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.util.List;
import java.util.stream.Collectors;
import org.jboss.logging.Logger;

/** Mapper to catch AppValidationException and return a detailed, grouped error response. */
@Provider
public class AppValidationExceptionMapper implements ExceptionMapper<AppValidationException> {

  private static final Logger LOG = Logger.getLogger(AppValidationExceptionMapper.class);

  @Inject I18n i18n;
  @Context HttpHeaders headers;

  /**
   * Maps AppValidationException to an HTTP 400 response with validation error details grouped by
   * their respective field names.
   *
   * @param exception The caught exception containing field validation errors.
   * @return An HTTP 400 response with grouped validation error details.
   */
  /** {@inheritDoc} */
  @Override
  public Response toResponse(AppValidationException exception) {
    LOG.debugf(exception, "Validation error caught");
    var locale = PresenterUtils.pickLocale(headers);
    List<FieldErrorsResponse> groupedErrors =
        CollectionUtils.toStream(exception.getFieldErrors())
            .collect(
                Collectors.groupingBy(
                    GenericFieldErrorCodes::getFieldName,
                    Collectors.mapping(this::mapToDetail, Collectors.toList())))
            .entrySet()
            .stream()
            .map(entry -> new FieldErrorsResponse(entry.getKey(), entry.getValue()))
            .toList();

    ApiError apiError =
        ApiError.of(
            SharedErrorCodes.VALIDATION_ERROR.getCode(),
            i18n.translation(SharedErrorCodes.VALIDATION_ERROR.getBundleKey(), locale),
            new Details(groupedErrors));

    return Response.status(Response.Status.BAD_REQUEST).entity(ApiEnvelope.error(apiError)).build();
  }

  private FieldErrorsResponse.FieldErrorDetail mapToDetail(GenericFieldErrorCodes fieldError) {
    String errorCode = fieldError.getCode();
    String message =
        i18n.translation(fieldError.getBundleKey(), PresenterUtils.pickLocale(headers));
    return new FieldErrorsResponse.FieldErrorDetail(errorCode, message);
  }
}
