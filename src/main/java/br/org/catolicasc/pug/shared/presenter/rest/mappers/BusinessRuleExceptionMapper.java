package br.org.catolicasc.pug.shared.presenter.rest.mappers;

import br.org.catolicasc.pug.shared.exceptions.BusinessRuleException;
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
 * Maps {@link BusinessRuleException} to an HTTP 422 (Unprocessable Entity) response.
 *
 * <p>Translates the specific domain business rule violation directly into a localized API error
 * response without exposing internal field names or values.
 */
@Provider
public class BusinessRuleExceptionMapper implements ExceptionMapper<BusinessRuleException> {

  private static final Logger LOG = Logger.getLogger(BusinessRuleExceptionMapper.class);

  @Inject I18n i18n;
  @Context HttpHeaders headers;

  /**
   * Converts a BusinessRuleException into a structured HTTP 422 response.
   *
   * @param ex The BusinessRuleException thrown by the domain.
   * @return A Response object containing the specific error code and localized reason.
   */
  /** {@inheritDoc} */
  @Override
  public Response toResponse(BusinessRuleException ex) {
    LOG.debugf(ex, "Business rule violation caught");
    String code = ex.getCode().getCode();
    String message =
        i18n.translation(ex.getCode().getBundleKey(), PresenterUtils.pickLocale(headers));

    ApiError error = ApiError.of(code, message);

    return Response.status(422)
        .type(MediaType.APPLICATION_JSON_TYPE)
        .entity(ApiEnvelope.error(error))
        .build();
  }
}
