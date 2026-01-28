package com.pug.shared.presenter.rest.mappers;

import com.pug.shared.domain.enums.SharedErrorCodes;
import com.pug.shared.exceptions.AppValidationException;
import com.pug.shared.i18n.I18n;
import com.pug.shared.presenter.rest.ApiEnvelope;
import com.pug.shared.presenter.rest.ApiError;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.util.List;
import java.util.Map;

/** Mapper to catch AppValidationException and return a detailed error response. */
@Provider
public class AppValidationExceptionMapper implements ExceptionMapper<AppValidationException> {

  @Inject I18n i18n;

  /**
   * Maps AppValidationException to an HTTP 400 response with validation error details.
   *
   * @param exception The caught exception.
   * @return An HTTP 400 response with validation error details.
   */
  @Override
  public Response toResponse(AppValidationException exception) {
    String mainErrorCodeName = SharedErrorCodes.VALIDATION_ERROR.name();
    String generalValidationMessage =
        i18n.translation(SharedErrorCodes.VALIDATION_ERROR.getBundleKey());

    List<ApiError.FieldError> fieldErrors =
        exception.getProblems().stream()
            .map(
                problem ->
                    new ApiError.FieldError(
                        problem.code().getFieldName(),
                        ((Enum<?>) problem.code()).name(),
                        i18n.translation(problem.code().getBundleKey())))
            .toList();

    Map<String, Object> detailsMap = Map.of("fieldErrors", fieldErrors);

    ApiError apiError = ApiError.of(mainErrorCodeName, generalValidationMessage, detailsMap);

    return Response.status(Response.Status.BAD_REQUEST).entity(ApiEnvelope.error(apiError)).build();
  }
}
