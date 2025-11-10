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
  COURSE_ALREADY_EXISTS("error.domain.academic.course.alreadyexists");

  private final String bundleKey;

  AcademicErrorCodes(String bundleKey) {
    this.bundleKey = bundleKey;
  }
}
