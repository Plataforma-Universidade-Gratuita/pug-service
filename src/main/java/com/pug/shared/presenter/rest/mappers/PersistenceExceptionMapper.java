package com.pug.shared.presenter.rest.mappers;

import com.pug.identity.domain.enums.IdentityErrorCodes;
import com.pug.shared.domain.enums.GenericErrorCodes;
import com.pug.shared.domain.enums.SharedErrorCodes;
import com.pug.shared.i18n.I18n;
import com.pug.shared.presenter.rest.ApiEnvelope;
import com.pug.shared.presenter.rest.ApiError;
import com.pug.shared.presenter.rest.Details;
import jakarta.inject.Inject;
import jakarta.persistence.PersistenceException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.util.LinkedHashMap;
import java.util.Map;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.exception.DataException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Exception mapper for handling PersistenceException and its common causes, such as constraint
 * violations and data exceptions. This mapper translates database-related exceptions into
 * meaningful API error responses with appropriate HTTP status codes and error details.
 *
 * <p>It inspects the underlying cause of the PersistenceException to determine if it's a known
 * constraint violation (like unique constraints) or other data-related issues, and constructs a
 * standardized API error response accordingly. If the exception does not match known patterns, it
 * falls back to a generic internal server error response.
 */
@Provider
public class PersistenceExceptionMapper implements ExceptionMapper<PersistenceException> {

  private static final Logger LOG = LoggerFactory.getLogger(PersistenceExceptionMapper.class);

  @Inject I18n i18n;

  /**
   * Maps PersistenceException to appropriate HTTP responses based on the underlying cause.
   *
   * <p>This mapper inspects the root cause of the PersistenceException to determine if it's a known
   * database constraint violation (like unique constraints) or other data-related issues, and
   * constructs a meaningful API error response accordingly.
   *
   * @param ex The PersistenceException thrown during database operations.
   * @return A Response object with an appropriate HTTP status code and error details.
   */
  @Override
  public Response toResponse(PersistenceException ex) {
    Throwable cause = ex.getCause();
    if (cause instanceof ConstraintViolationException cve) {
      String constraintName = cve.getConstraintName();
      return handleConstraintViolation(constraintName, cve);
    }
    if (cause instanceof DataException) {
      return buildGenericInternalError(ex, "Data truncation or invalid format");
    }
    return buildGenericInternalError(ex, "Generic persistence error");
  }

  /**
   * Handles known database constraint violations by mapping them to specific API error responses.
   *
   * <p>This method checks the name of the violated constraint against known patterns (both Postgres
   * default and custom annotation names) to determine the appropriate error code and message to
   * return. If the constraint is not recognized, it falls back to a generic data integrity error
   * response.
   *
   * @param constraintName The name of the violated database constraint.
   * @param ex The original ConstraintViolationException for logging and fallback purposes.
   * @return A Response object with a conflict status and specific error details if recognized, or a
   *     generic data integrity error if not.
   */
  private Response handleConstraintViolation(
      String constraintName, ConstraintViolationException ex) {
    if (constraintName == null) {
      return buildGenericDataIntegrityResponse(ex);
    }
    // --- IDENTITY DOMAIN ---
    if (isConstraint(constraintName, "users_cpf_key", "uq_users_cpf")) {
      return buildConflictResponse(IdentityErrorCodes.USER_ALREADY_EXISTS, "cpf");
    }
    if (isConstraint(constraintName, "accounts_email_key", "uq_accounts_email")) {
      return buildConflictResponse(IdentityErrorCodes.ACCOUNT_ALREADY_EXISTS, "email");
    }
    // --- GEO DOMAIN ---
    if (isConstraint(constraintName, "cities_ibge_code_key", "uq_cities_ibge_code")) {
      return buildConflictResponse(
          SharedErrorCodes.DUPLICATED_RESOURCE_ERROR,
          "ibgeCode",
          "City with this IBGE code already exists");
    }
    // --- PARTNER DOMAIN ---
    if (isConstraint(constraintName, "entities_cnpj_key", "uq_entities_cnpj")) {
      return buildConflictResponse(
          SharedErrorCodes.DUPLICATED_RESOURCE_ERROR,
          "cnpj",
          "Entity with this CNPJ already exists");
    }
    // --- ACADEMIC DOMAIN ---
    if (isConstraint(constraintName, "schools_name_key", "uq_schools_name")) {
      return buildConflictResponse(
          SharedErrorCodes.DUPLICATED_RESOURCE_ERROR,
          "name",
          "School with this name already exists");
    }
    if (isConstraint(constraintName, "courses_name_key", "uq_courses_name")) {
      return buildConflictResponse(
          SharedErrorCodes.DUPLICATED_RESOURCE_ERROR,
          "name",
          "Course with this name already exists");
    }
    if (isConstraint(
        constraintName, "students_academic_registration_key", "uq_students_registration")) {
      return buildConflictResponse(
          SharedErrorCodes.DUPLICATED_RESOURCE_ERROR,
          "academicRegistration",
          "Student with this registration already exists");
    }
    // --- PROJECTS ---
    if (isConstraint(constraintName, "projects_entity_id_name_key", "uq_projects_entity_name")) {
      return buildConflictResponse(
          SharedErrorCodes.DUPLICATED_RESOURCE_ERROR,
          "name",
          "Project with this name already exists for this Entity");
    }
    if (isConstraint(constraintName, "attendances_qr_validation_hash_key", "uq_attendances_hash")) {
      return buildConflictResponse(
          SharedErrorCodes.DUPLICATED_RESOURCE_ERROR,
          "qrValidationHash",
          "Attendance already recorded (Duplicate QR Hash)");
    }
    if (constraintName.endsWith("_fkey")) {
      return buildDataIntegrityResponse(
          SharedErrorCodes.DATA_INTEGRITY_ERROR,
          "Cannot proceed because this resource is referenced by other records.");
    }
    return buildGenericDataIntegrityResponse(ex);
  }

  /**
   * Validates if the given constraint name matches either the raw Postgres constraint name or the
   * custom.
   *
   * @param rawName The raw constraint name as defined in the database (e.g., "users_cpf_key").
   * @param postgresName The expected Postgres constraint name to compare against.
   * @param customName The expected custom constraint name (e.g., from @UniqueConstraint) to compare
   *     against.
   * @return true if the rawName matches either the postgresName or the customName
   *     (case-insensitive), false otherwise.
   */
  private boolean isConstraint(String rawName, String postgresName, String customName) {
    return rawName.equalsIgnoreCase(postgresName) || rawName.equalsIgnoreCase(customName);
  }

  /**
   * Builds a conflict response for a duplicated resource error, using the provided error code and
   * field name.
   *
   * <p>This method constructs a standardized API error response indicating that a resource with the
   * specified field value already exists, based on the provided error code which should correspond
   * to a specific duplication scenario. It uses internationalization to fetch the appropriate
   * messages for both the main error and the specific reason.
   *
   * @param code The specific error code representing the duplication scenario (e.g.,
   *     USER_ALREADY_EXISTS).
   * @param fieldName The name of the field that caused the duplication conflict (e.g., "cpf").
   * @return A Response object with a conflict status and detailed error information.
   */
  private Response buildConflictResponse(GenericErrorCodes code, String fieldName) {
    return buildConflictResponse(code, fieldName, null);
  }

  /**
   * Builds a conflict response for a duplicated resource error, using the provided error code,
   * field name, and fallback message.
   *
   * <p>This method constructs a standardized API error response indicating that a resource with the
   * specified field value already exists, based on the provided error code which should correspond
   * to a specific duplication scenario. It uses internationalization to fetch the appropriate
   * messages for both the main error and the specific reason. If the specific reason cannot be
   * translated, it falls back to the provided fallback message.
   *
   * @param code The specific error code representing the duplication scenario (e.g.,
   *     USER_ALREADY_EXISTS).
   * @param fieldName The name of the field that caused the duplication conflict (e.g., "cpf").
   * @param fallbackMessage A fallback message to use if the specific reason cannot be translated.
   * @return A Response object with a conflict status and detailed error information.
   */
  private Response buildConflictResponse(
      GenericErrorCodes code, String fieldName, String fallbackMessage) {
    String specificReason = i18n.translation(code.getBundleKey());
    if (specificReason.equals(code.getBundleKey()) && fallbackMessage != null) {
      specificReason = fallbackMessage;
    }

    Map<String, Object> details = new LinkedHashMap<>();
    details.put("field", fieldName);
    details.put("rejectedValue", "unknown");
    details.put("reason", specificReason);

    String mainMessage =
        i18n.translation(SharedErrorCodes.DUPLICATED_RESOURCE_ERROR.getBundleKey());

    ApiError error =
        ApiError.of(
            SharedErrorCodes.DUPLICATED_RESOURCE_ERROR.name(), mainMessage, new Details(details));

    return Response.status(Response.Status.CONFLICT)
        .type(MediaType.APPLICATION_JSON_TYPE)
        .entity(ApiEnvelope.error(error))
        .build();
  }

  /**
   * Builds a conflict response for a data integrity violation, using the provided error code and
   * message.
   *
   * <p>This method constructs a standardized API error response indicating that a data integrity
   * violation occurred, based on the provided error code which should correspond to a specific
   * integrity issue. It uses internationalization to fetch the appropriate messages for both the
   * main error and the specific reason.
   *
   * @param code The specific error code representing the data integrity issue (e.g.,
   *     DATA_INTEGRITY_ERROR).
   * @param message A detailed message explaining the reason for the data integrity violation.
   * @return A Response object with a conflict status and detailed error information.
   */
  private Response buildDataIntegrityResponse(GenericErrorCodes code, String message) {
    ApiError error =
        ApiError.of(
            ((Enum<?>) code).name(),
            i18n.translation(code.getBundleKey()),
            new Details(Map.of("reason", message)));
    return Response.status(Response.Status.CONFLICT)
        .type(MediaType.APPLICATION_JSON_TYPE)
        .entity(ApiEnvelope.error(error))
        .build();
  }

  /**
   * Builds a generic data integrity error response for unrecognized constraint violations.
   *
   * <p>This method is used as a fallback when a ConstraintViolationException is encountered with an
   * unrecognized constraint name. It logs the unknown constraint for debugging purposes and returns
   * a standardized API error response indicating that a data integrity violation occurred, without
   * specific details about the violated constraint.
   *
   * @param ex The original ConstraintViolationException containing information about the violation.
   * @return A Response object with a conflict status and a generic data integrity error message.
   */
  private Response buildGenericDataIntegrityResponse(ConstraintViolationException ex) {
    LOG.warn("Unknown database constraint violation: {}", ex.getConstraintName());
    String msg = i18n.translation(SharedErrorCodes.DATA_INTEGRITY_ERROR.getBundleKey());

    Map<String, Object> details = new LinkedHashMap<>();
    details.put("constraint", ex.getConstraintName());
    details.put("detail", "Database constraint violation occurred.");

    ApiError error =
        ApiError.of(SharedErrorCodes.DATA_INTEGRITY_ERROR.name(), msg, new Details(details));
    return Response.status(Response.Status.CONFLICT)
        .type(MediaType.APPLICATION_JSON_TYPE)
        .entity(ApiEnvelope.error(error))
        .build();
  }

  /**
   * Builds a generic internal server error response for unhandled persistence exceptions.
   *
   * <p>This method is used as a fallback for PersistenceExceptions that do not match known
   * constraint violations or data issues. It logs the exception for debugging purposes and returns
   * a standardized API error response indicating that an internal error occurred, without exposing
   * sensitive details.
   *
   * @param ex The original PersistenceException that was thrown.
   * @param note A brief note describing the context of the error for logging purposes.
   * @return A Response object with an internal server error status and a generic error message.
   */
  private Response buildGenericInternalError(PersistenceException ex, String note) {
    LOG.error("Persistence error: {}", note, ex);
    String msg = i18n.translation(SharedErrorCodes.INTERNAL_ERROR.getBundleKey());

    Map<String, Object> details = new LinkedHashMap<>();
    details.put("exception", ex.getClass().getSimpleName());

    ApiError error = ApiError.of(SharedErrorCodes.INTERNAL_ERROR.name(), msg, new Details(details));
    return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
        .type(MediaType.APPLICATION_JSON_TYPE)
        .entity(ApiEnvelope.error(error))
        .build();
  }
}
