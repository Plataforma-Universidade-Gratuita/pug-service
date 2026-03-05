package com.pug.shared.presenter.rest.mappers;

import com.pug.shared.domain.enums.GenericFieldErrorCodes;
import com.pug.shared.domain.enums.SharedErrorCodes;
import com.pug.shared.exceptions.AppValidationException;
import com.pug.shared.i18n.I18n;
import com.pug.shared.presenter.rest.ApiEnvelope;
import com.pug.shared.presenter.rest.ApiError;
import com.pug.shared.presenter.rest.Details;
import com.pug.shared.presenter.rest.FieldErrorsResponse;
import com.pug.shared.utils.CollectionUtils;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.util.List;
import java.util.stream.Collectors;

/** Mapper to catch AppValidationException and return a detailed, grouped error response. */
@Provider
public class AppValidationExceptionMapper implements ExceptionMapper<AppValidationException> {

  @Inject I18n i18n;

  /**
   * Maps AppValidationException to an HTTP 400 response with validation error details grouped by
   * their respective field names.
   *
   * @param exception The caught exception containing field validation errors.
   * @return An HTTP 400 response with grouped validation error details.
   */
  @Override
  public Response toResponse(AppValidationException exception) {
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
            i18n.translation(SharedErrorCodes.VALIDATION_ERROR.getBundleKey()),
            new Details(groupedErrors));

    return Response.status(Response.Status.BAD_REQUEST).entity(ApiEnvelope.error(apiError)).build();
  }

  /**
   * Helper method to convert a GenericFieldErrorCodes into a nested FieldErrorDetail.
   *
   * @param fieldError The domain validation error.
   * @return The detailed record containing the raw code and the translated message.
   */
  private FieldErrorsResponse.FieldErrorDetail mapToDetail(GenericFieldErrorCodes fieldError) {
    String errorCode = fieldError.getCode();
    String message = i18n.translation(fieldError.getBundleKey());
    return new FieldErrorsResponse.FieldErrorDetail(errorCode, message);
  }
}
