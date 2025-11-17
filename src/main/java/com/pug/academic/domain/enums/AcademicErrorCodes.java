package com.pug.academic.domain.enums;

import com.pug.shared.domain.enums.GenericErrorCodes;
import lombok.Getter;

/**
 * Enum representing error codes specific to the academic domain.
 *
 * <p>Each error code is associated with a specific validation failure scenario and has a {@code
 * bundleKey} that results into a located error message.
 */
@Getter
public enum AcademicErrorCodes implements GenericErrorCodes {
  COURSE_ALREADY_EXISTS("academic.error.course.already.exists"),
  COURSE_NOT_FOUND("academic.error.course.not.found"),
  INVALID_CAMPUS_BLANK("academic.error.campus.blank"),
  INVALID_COURSE_NAME_BLANK("academic.error.course.name.blank"),
  INVALID_COURSE_NAME_LENGTH("academic.error.course.name.length"),
  INVALID_COURSE_BLANK("academic.error.course.blank"),
  INVALID_HOURS_BLANK("academic.error.hours.blank"),
  INVALID_HOURS_COMPLETED_GT_REQUIRED("academic.error.hours.completed.gt.required"),
  INVALID_PERIOD_BLANK("academic.error.period.blank"),
  INVALID_PERIOD_RANGE("academic.error.period.range"),
  INVALID_REGISTRATION_BLANK("academic.error.registration.blank"),
  INVALID_REGISTRATION_LENGTH("academic.error.registration.length"),
  INVALID_SCHOOL_NAME_BLANK("academic.error.school.name.blank"),
  INVALID_SCHOOL_NAME_LENGTH("academic.error.school.name.length"),
  INVALID_SCHOOL_BLANK("academic.error.school.blank"),
  INVALID_STUDENT_ACCOUNT_BLANK("academic.error.student.account.blank"),
  SCHOOL_ALREADY_EXISTS("academic.error.school.already.exists"),
  SCHOOL_NOT_FOUND("academic.error.school.not.found"),
  STUDENT_ALREADY_EXISTS("academic.error.student.already.exists"),
  STUDENT_NOT_FOUND("academic.error.student.not.found");

  private final String bundleKey;

  AcademicErrorCodes(String bundleKey) {
    this.bundleKey = bundleKey;
  }
}
