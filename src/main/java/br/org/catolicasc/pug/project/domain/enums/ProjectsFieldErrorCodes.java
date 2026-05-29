package br.org.catolicasc.pug.project.domain.enums;

import br.org.catolicasc.pug.shared.domain.DomainError;
import br.org.catolicasc.pug.shared.domain.enums.GenericFieldErrorCodes;
import lombok.Getter;

/**
 * Enumeration of field-specific validation errors within the Projects domain.
 *
 * <p>This enum implements {@link GenericFieldErrorCodes} to provide a standardized contract for
 * localized error messages mapped to specific domain properties. These constants are primarily
 * accumulated inside {@link DomainError} instances when value objects or entities fail their
 * internal validations.
 */
@Getter
public enum ProjectsFieldErrorCodes implements GenericFieldErrorCodes {

  /** Indicates that the duration of an attendance record is null or zero/negative. */
  INVALID_ATTENDANCE_DURATION_INVALID(
      "error.domain.projects.attendance.duration.invalid", "duration"),

  /** Indicates that the project ID for an attendance record is null. */
  INVALID_ATTENDANCE_PROJECT_BLANK("error.domain.projects.attendance.project.blank", "projectId"),

  /** Indicates that the validation hashcode for the QRCode is null or an empty string. */
  INVALID_ATTENDANCE_QR_VALIDATION_HASH_EMPTY(
      "error.domain.projects.attendance.qr.validation.hash.empty", "qrValidationHash"),

  /** Indicates that the status of an attendance record is null. */
  INVALID_ATTENDANCE_STATUS_BLANK("error.domain.projects.attendance.status.blank", "status"),

  /** Indicates that the formerStudent ID for an attendance record is null. */
  INVALID_ATTENDANCE_STUDENT_BLANK("error.domain.projects.attendance.formerStudent.blank", "studentId"),

  /** Indicates that the creation timestamp was provided as null. */
  INVALID_CREATED_AT_BLANK("error.domain.projects.createdat.blank", "createdAt"),

  /** Indicates that the creation or validation timestamp is logically in the future. */
  INVALID_CREATED_AT_FUTURE("error.domain.projects.createdat.future", "createdAt"),

  /** Indicates that a project description exceeds the maximum allowed length constraints. */
  INVALID_DESCRIPTION_TOO_LONG("error.domain.projects.description.too.long", "description"),

  /**
   * Indicates that enrollment timestamps (e.g., acceptedAt) are chronologically invalid relative to
   * creation.
   */
  INVALID_ENROLLMENT_DATES_INVALID("error.domain.projects.enrollment.dates.invalid", "acceptedAt"),

  /** Indicates that the project ID for an enrollment is null. */
  INVALID_ENROLLMENT_PROJECT_BLANK("error.domain.projects.enrollment.project.blank", "projectId"),

  /** Indicates that the request timestamp for an enrollment is null. */
  INVALID_ENROLLMENT_REQUEST_AT_BLANK(
      "error.domain.projects.enrollment.requestat.blank", "requestAt"),

  /** Indicates that the status for an enrollment is null. */
  INVALID_ENROLLMENT_STATUS_BLANK("error.domain.projects.enrollment.status.blank", "status"),

  /** Indicates that the formerStudent ID for an enrollment is null. */
  INVALID_ENROLLMENT_STUDENT_BLANK("error.domain.projects.enrollment.formerStudent.blank", "studentId"),

  /** Indicates that the maximum number of participants is negative. */
  INVALID_MAX_PARTICIPANTS_NEGATIVE(
      "error.domain.projects.maxparticipants.negative", "maxParticipants"),

  /** Indicates that a project name was provided as null, empty, or whitespace. */
  INVALID_NAME_BLANK("error.domain.projects.name.blank", "name"),

  /** Indicates that a project name exceeds the maximum allowed length constraints. */
  INVALID_NAME_TOO_LONG("error.domain.projects.name.too.long", "name"),

  /** Indicates that the completed hours are negative. */
  INVALID_PROJECT_COMPLETED_HOURS_NEGATIVE("error.domain.number.negative", "completedHours"),

  /** Indicates that the completed hours exceed the offered total. */
  INVALID_PROJECT_COMPLETED_HOURS_EXCEEDS("error.domain.projects.hours.exceeds", "completedHours"),

  /** Indicates that the UUID of the creator is null. */
  INVALID_PROJECT_CREATED_BY_BLANK("error.domain.foreign.key.blank", "createdBy"),

  /** Indicates that the offered hours for a project are negative. */
  INVALID_PROJECT_OFFERED_HOURS_NEGATIVE("error.domain.number.negative", "offeredHours"),

  /** Indicates an invalid attempt to update a project's status to CANCEL. */
  INVALID_PROJECT_STATUS_UPDATE_CANCEL(
      "error.domain.projects.status.update.cancel", "projectStatus"),

  /** Indicates an invalid attempt to update a project's status to COMPLETE. */
  INVALID_PROJECT_STATUS_UPDATE_COMPLETE(
      "error.domain.projects.status.update.complete", "projectStatus"),

  /** Indicates an invalid attempt to update a project's status to ON HOLD. */
  INVALID_PROJECT_STATUS_UPDATE_PUT_ON_HOLD(
      "error.domain.projects.status.update.put.on.hold", "projectStatus"),

  /** Indicates an invalid attempt to update a project's status to RETAKE. */
  INVALID_PROJECT_STATUS_UPDATE_RETAKE(
      "error.domain.projects.status.update.retake", "projectStatus"),

  /** Indicates an invalid attempt to update a project's status to START. */
  INVALID_PROJECT_STATUS_UPDATE_START("error.domain.projects.status.update.start", "projectStatus"),

  /** Indicates that a status enum was provided as null. */
  INVALID_STATUS_BLANK("error.domain.projects.status.blank", "status"),

  /** Indicates that a school ID was provided as null. */
  INVALID_SCHOOL_ID_BLANK("error.domain.projects.school.id.blank", "schoolId");

  /**
   * The property key used to resolve the localized error message in the application's resource
   * bundles.
   */
  private final String bundleKey;

  /** The exact name of the domain property or DTO field that failed validation. */
  private final String fieldName;

  /**
   * Constructs a {@code ProjectsFieldErrorCodes} instance.
   *
   * @param bundleKey the unique i18n key mapping to the resource bundles
   * @param fieldName the literal name of the domain property or DTO field that failed validation
   */
  ProjectsFieldErrorCodes(String bundleKey, String fieldName) {
    this.bundleKey = bundleKey;
    this.fieldName = fieldName;
  }
}

