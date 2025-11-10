package com.pug.academic.domain.enums;

import com.pug.shared.errors.GenericErrorCodes;
import lombok.Getter;

/** Enum representing error codes specific to the academic domain. */
@Getter
public enum AcademicErrorCodes implements GenericErrorCodes {
  INVALID_SCHOOL_NAME_BLANK("academic.error.invalid_school_name_blank"),
  INVALID_SCHOOL_NAME_TOOLONG("academic.error.invalid_school_name_toolong"),
  SCHOOL_NOT_FOUND("error.domain.academic.school.notfound"),
  SCHOOL_ALREADY_EXISTS("error.domain.academic.school.alreadyexists"),
  INVALID_COURSE_NAME_BLANK("error.domain.academic.course-name.blank"),
  INVALID_COURSE_NAME_TOOLONG("error.domain.academic.course-name.toolong"),
  INVALID_SCHOOL("error.domain.academic.school.invalid"),
  COURSE_NOT_FOUND("error.domain.academic.course.notfound"),
  COURSE_ALREADY_EXISTS("error.domain.academic.course.alreadyexists"),
  INVALID_STUDENT_USER("error.domain.academic.student.user"),
  INVALID_REGISTRATION("error.domain.academic.registration"),
  INVALID_REGISTRATION_TOOLONG("error.domain.academic.registration.toolong"),
  INVALID_CAMPUS("error.domain.academic.campus"),
  INVALID_COURSE("error.domain.academic.course"),
  INVALID_HOURS("error.domain.academic.hours"),
  INVALID_HOURS_COMPLETED_GT_REQUIRED("error.domain.academic.hours.completed.gt.required"),
  INVALID_PERIOD("error.domain.academic.period"),
  INVALID_PERIOD_RANGE("error.domain.academic.period.range"),
  STUDENT_ALREADY_EXISTS("error.domain.academic.student.alreadyexists"),
  STUDENT_NOT_FOUND("error.domain.academic.student.notfound");

  private final String bundleKey;

  AcademicErrorCodes(String bundleKey) {
    this.bundleKey = bundleKey;
  }
}
