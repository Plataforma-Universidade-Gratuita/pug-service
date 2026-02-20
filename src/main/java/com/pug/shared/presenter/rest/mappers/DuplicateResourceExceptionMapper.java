package com.pug.shared.presenter.rest.mappers;

import com.pug.shared.domain.enums.SharedErrorCodes;
import com.pug.shared.exceptions.DuplicateResourceException;
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
 * Maps DuplicateResourceException to an HTTP 409 (Conflict) response.
 */
@Provider
public class DuplicateResourceExceptionMapper
        implements ExceptionMapper<DuplicateResourceException> {

  @Inject
  I18n i18n;

  /**
   * Converts a DuplicateResourceException into a structured HTTP response.
   * <p>
   * The response includes:
   * - A top-level message indicating a resource conflict.
   * - A specific reason for the conflict based on the error code.
   * - Details about the conflicting field and value.
   *
   * @param ex The DuplicateResourceException to convert.
   * @return A Response object with status 409 and a JSON body containing error details.
   */
  @Override
  public Response toResponse(DuplicateResourceException ex) {
    String mainMessage = i18n.translation(SharedErrorCodes.DUPLICATED_RESOURCE_ERROR.getBundleKey());
    String specificReason = i18n.translation(ex.getCode().getBundleKey());

    Map<String, Object> conflictDetails = new LinkedHashMap<>();
    conflictDetails.put("field", ex.getConflictingField());
    conflictDetails.put("rejectedValue", ex.getConflictingValue());
    conflictDetails.put("reason", specificReason);

    ApiError error = ApiError.of(
            SharedErrorCodes.DUPLICATED_RESOURCE_ERROR.name(),
            mainMessage,
            new Details(conflictDetails)
    );

    return Response.status(Response.Status.CONFLICT)
            .type(MediaType.APPLICATION_JSON_TYPE)
            .entity(ApiEnvelope.error(error))
            .build();
  }
}