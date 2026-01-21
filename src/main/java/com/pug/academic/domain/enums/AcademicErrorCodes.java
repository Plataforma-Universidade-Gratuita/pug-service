package com.pug.academic.domain.enums;

import com.pug.shared.domain.enums.GenericErrorCodes;
import lombok.Getter;

/**
 * Enum representing error codes specific to the academic domain.
 *
 * <p>Each error code is associated with a specific validation failure scenario and has a {@code
 * bundleKey} that results into a localized error message. It also includes a {@code fieldName}
 * property to identify the specific field related to the error, if applicable.
 */
@Getter
public enum AcademicErrorCodes implements GenericErrorCodes {
  INVALID_ID_BLANK("error.domain.academic.id.blank", "id"),
  INVALID_COURSE_BLANK("error.domain.academic.course.blank", "courseId"),
  INVALID_COURSE_NAME_BLANK("error.domain.academic.course.name.blank", "name"),
  INVALID_COURSE_NAME_LENGTH("error.domain.academic.course.name.length", "name"),
  INVALID_CAMPUS_BLANK("error.domain.academic.campus.blank", "campus"),
  INVALID_HOURS_BLANK("error.domain.academic.hours.blank", "hours"),
  INVALID_HOURS_COMPLETED_GT_REQUIRED("error.domain.academic.hours.completed.gt.required", "completedHours"),
  INVALID_PERIOD_BLANK("error.domain.academic.period.blank", "period"),
  INVALID_PERIOD_RANGE("error.domain.academic.period.range", "period"),
  INVALID_REGISTRATION_BLANK("error.domain.academic.registration.blank", "registration"),
  INVALID_REGISTRATION_LENGTH("error.domain.academic.registration.length", "registration"),
  INVALID_SCHOOL_BLANK("error.domain.academic.school.blank", "schoolId"),
  INVALID_SCHOOL_NAME_BLANK("error.domain.academic.school.name.blank", "name"),
  INVALID_SCHOOL_NAME_LENGTH("error.domain.academic.school.name.length", "name"),
  INVALID_STUDENT_ACCOUNT_BLANK("error.domain.academic.student.account.blank", "accountId"),

  COURSE_ALREADY_EXISTS("error.domain.academic.course.already.exists", null),
  COURSE_NOT_FOUND("error.domain.academic.course.not.found", null),
  SCHOOL_ALREADY_EXISTS("error.domain.academic.school.already.exists", null),
  SCHOOL_NOT_FOUND("error.domain.academic.school.not.found", null),
  STUDENT_ALREADY_EXISTS("error.domain.academic.student.already.exists", null),
  STUDENT_NOT_FOUND("error.domain.academic.student.not.found", null),

  SCHOOL_STILL_REFERENCED("error.domain.academic.school.still.referenced", null),
  COURSE_STILL_REFERENCED("error.domain.academic.course.still.referenced", null),
  STUDENT_STILL_REFERENCED("error.domain.academic.student.still.referenced", null); // Example if Student is referenced by other modules


  private final String bundleKey;
  private final String fieldName;

  /**
   * Constructor for the AcademicErrorCodes enum.
   *
   * @param bundleKey The internationalization resource key associated with the error.
   * @param fieldName The name of the field associated with the error, or null if not field-specific.
   */
  AcademicErrorCodes(String bundleKey, String fieldName) {
    this.bundleKey = bundleKey;
    this.fieldName = fieldName;
  }
}