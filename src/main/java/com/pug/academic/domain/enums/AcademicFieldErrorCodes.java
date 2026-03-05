package com.pug.academic.domain.enums;

import com.pug.shared.domain.enums.GenericFieldErrorCodes;
import lombok.Getter;

/**
 * Enumeration of field-specific validation errors within the Academic domain.
 *
 * <p>This enum implements {@link GenericFieldErrorCodes} to provide a standardized contract for
 * localized error messages mapped to specific domain properties (e.g., "registration", "period").
 * These constants are primarily accumulated inside {@link com.pug.shared.domain.DomainError}
 * instances when value objects or entities fail their internal validations.
 */
@Getter
public enum AcademicFieldErrorCodes implements GenericFieldErrorCodes {

  /** Indicates that an account ID was provided as null. */
  INVALID_ACCOUNT_ID_BLANK("error.domain.academic.accountId.blank", "accountId"),

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

  /** Indicates that a school ID was provided as null. */
  INVALID_SCHOOL_BLANK("error.domain.academic.school.blank", "schoolId");

  /**
   * The property key used to resolve the localized error message in the application's resource
   * bundles.
   */
  private final String bundleKey;

  /** The exact name of the domain property or DTO field that failed validation. */
  private final String fieldName;

  /**
   * Constructs an {@code AcademicFieldErrorCodes} instance.
   *
   * @param bundleKey the unique i18n key mapping to the resource bundles
   * @param fieldName the literal name of the domain property or DTO field that failed validation
   */
  AcademicFieldErrorCodes(String bundleKey, String fieldName) {
    this.bundleKey = bundleKey;
    this.fieldName = fieldName;
  }
}
