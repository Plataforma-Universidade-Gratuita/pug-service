package br.org.catolicasc.pug.academic.domain.enums;

import br.org.catolicasc.pug.shared.domain.DomainError;
import br.org.catolicasc.pug.shared.domain.enums.GenericFieldErrorCodes;
import lombok.Getter;

/**
 * Enumeration of field-specific validation errors within the Academic domain.
 *
 * <p>This enum implements {@link GenericFieldErrorCodes} to provide a standardized contract for
 * localized error messages mapped to specific domain properties (e.g., "registration", "period").
 * These constants are primarily accumulated inside {@link DomainError} instances when value objects
 * or entities fail their internal validations.
 */
@Getter
public enum AcademicFieldErrorCodes implements GenericFieldErrorCodes {

  /** Indicates that an account ID was provided as null. */
  INVALID_ACCOUNT_ID_BLANK("error.domain.academic.accountId.blank", "accountId"),

  /** Indicates that the completed hours are negative. */
  INVALID_COMPLETED_HOURS_NEGATIVE("error.domain.number.negative", "completedHours"),

  /** Indicates that the completed hours exceed the required total. */
  INVALID_COMPLETED_HOURS_EXCEEDS("error.domain.academic.hours.exceeds", "completedHours"),

  /** Indicates that a course ID was provided as null. */
  INVALID_COURSE_BLANK("error.domain.academic.course.blank", "courseId"),

  /** Indicates that hours were provided as null. */
  INVALID_HOURS_BLANK("error.domain.academic.hours.blank", "hours"),

  /** Indicates that the required hours were negative, which is not allowed. */
  INVALID_REQUIRED_HOURS_NEGATIVE("error.domain.number.negative", "requiredHours"),

  /** Indicates that the required hours were exactly zero, which is not allowed. */
  INVALID_REQUIRED_HOURS_ZERO("error.domain.number.zero", "requiredHours"),

  /** Indicates that a period object itself was provided as null. */
  INVALID_PERIOD_BLANK("error.domain.academic.period.blank", "period"),

  /** Indicates that the start date of a period was provided as null. */
  INVALID_START_DATE_BLANK("error.domain.field.blank", "startDate"),

  /** Indicates that the due date of a period was provided as null. */
  INVALID_DUE_DATE_BLANK("error.domain.field.blank", "dueDate"),

  /** Indicates that the due date is chronologically before the start date. */
  INVALID_PERIOD_RANGE("error.domain.period.range", "period"),

  /** Indicates that an academic registration string was provided as null, empty, or whitespace. */
  INVALID_REGISTRATION_BLANK("error.domain.academic.registration.blank", "registration"),

  /**
   * Indicates that an academic registration string exceeds the maximum allowed length constraints.
   */
  INVALID_REGISTRATION_TOO_LONG("error.domain.academic.registration.tooLong", "registration"),

  /** Indicates that a area of expertise ID was provided as null. */
  INVALID_AREA_OF_EXPERTISE_BLANK(
      "error.domain.academic.areaOfExpertise.blank", "areaOfExpertiseId");

  private final String bundleKey;

  private final String fieldName;

  AcademicFieldErrorCodes(String bundleKey, String fieldName) {
    this.bundleKey = bundleKey;
    this.fieldName = fieldName;
  }
}
