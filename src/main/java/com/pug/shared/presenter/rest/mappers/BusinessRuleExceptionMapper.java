package com.pug.shared.presenter.rest.mappers;

import com.pug.shared.domain.enums.SharedErrorCodes;
import com.pug.shared.exceptions.BusinessRuleException;
import com.pug.shared.i18n.I18n;
import com.pug.shared.presenter.rest.ApiEnvelope;
import com.pug.shared.presenter.rest.ApiError;
import com.pug.shared.presenter.rest.Details;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Maps BusinessRuleException to an HTTP 422 (Unprocessable Entity) response.
 */
@Provider
public class BusinessRuleExceptionMapper
        implements ExceptionMapper<BusinessRuleException> {

  @Inject
  I18n i18n;

  @Override
  public Response toResponse(BusinessRuleException ex) {
    String mainMessage =
            i18n.translation(SharedErrorCodes.BUSINESS_RULE_ERROR.getBundleKey());

    String specificReason = i18n.translation(ex.getCode().getBundleKey());

    Map<String, Object> ruleDetails = new LinkedHashMap<>();
    ruleDetails.put("field", ex.getField());
    ruleDetails.put("value", ex.getValue());
    ruleDetails.put("reason", specificReason);

    ApiError error =
            ApiError.of(
                    SharedErrorCodes.BUSINESS_RULE_ERROR.name(),
                    mainMessage,
                    new Details(ruleDetails));

    return Response.status(422)
            .type(MediaType.APPLICATION_JSON_TYPE)
            .entity(ApiEnvelope.error(error))
            .build();
  }
}