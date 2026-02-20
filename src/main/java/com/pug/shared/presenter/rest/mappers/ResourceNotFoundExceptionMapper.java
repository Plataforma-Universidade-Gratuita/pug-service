package com.pug.shared.presenter.rest.mappers;

import com.pug.shared.domain.enums.SharedErrorCodes;
import com.pug.shared.exceptions.ResourceNotFoundException;
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
 * Maps ResourceNotFoundException to an HTTP 404 response.
 */
@Provider
public class ResourceNotFoundExceptionMapper implements ExceptionMapper<ResourceNotFoundException> {

  @Inject
  I18n i18n;

  @Override
  public Response toResponse(ResourceNotFoundException ex) {
    String mainMessage = i18n.translation(SharedErrorCodes.RESOURCE_NOT_FOUND_ERROR.getBundleKey());
    String specificReason = i18n.translation(ex.getCode().getBundleKey());

    Map<String, Object> notFoundDetails = new LinkedHashMap<>();
    if (ex.getSearchField() != null) {
      notFoundDetails.put("field", ex.getSearchField());
    }
    if (ex.getSearchValue() != null) {
      notFoundDetails.put("rejectedValue", ex.getSearchValue());
    }
    notFoundDetails.put("reason", specificReason);

    ApiError error = ApiError.of(
            SharedErrorCodes.RESOURCE_NOT_FOUND_ERROR.name(),
            mainMessage,
            new Details(notFoundDetails)
    );

    return Response.status(Response.Status.NOT_FOUND)
            .type(MediaType.APPLICATION_JSON_TYPE)
            .entity(ApiEnvelope.error(error))
            .build();
  }
}