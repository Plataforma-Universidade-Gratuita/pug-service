package com.pug.shared.presenter.rest.mappers;

import com.pug.shared.exceptions.BusinessRuleException;
import com.pug.shared.i18n.I18n;
import com.pug.shared.presenter.rest.ApiEnvelope;
import com.pug.shared.presenter.rest.ApiError;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

/**
 * Maps {@link BusinessRuleException} to an HTTP 422 (Unprocessable Entity) response.
 * <p>
 * Translates the specific domain business rule violation directly into a localized
 * API error response without exposing internal field names or values.
 */
@Provider
public class BusinessRuleExceptionMapper implements ExceptionMapper<BusinessRuleException> {

  @Inject
  I18n i18n;

  /**
   * Converts a BusinessRuleException into a structured HTTP 422 response.
   *
   * @param ex The BusinessRuleException thrown by the domain.
   * @return A Response object containing the specific error code and localized reason.
   */
  @Override
  public Response toResponse(BusinessRuleException ex) {
    String code = ex.getCode().getCode();
    String message = i18n.translation(ex.getCode().getBundleKey());

    ApiError error = ApiError.of(code, message);

    return Response.status(422)
            .type(MediaType.APPLICATION_JSON_TYPE)
            .entity(ApiEnvelope.error(error))
            .build();
  }
}