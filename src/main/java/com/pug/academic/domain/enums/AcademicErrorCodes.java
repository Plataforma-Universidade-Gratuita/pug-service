package com.pug.academic.domain.enums;

import com.pug.shared.domain.enums.GenericCodes;
import lombok.Getter;

/**
 * Enumeration of high-level domain error codes specific to the Academic context.
 *
 * <p>This enum implements {@link GenericCodes} to map business rule violations and resource state
 * conflicts directly to localized messages in the application's resource bundles. Unlike
 * field-level validations, these codes represent aggregate-level or cross-cutting system states
 * (e.g., duplication, structural integrity, or missing records).
 */
@Getter
public enum AcademicErrorCodes implements GenericCodes {

  /**
   * Indicates an attempt to create or update an academic course using a name that is already
   * registered in the system.
   */
  COURSE_ALREADY_EXISTS("error.domain.academic.course.already.exists"),

  /**
   * Indicates that a requested academic course could not be located in the underlying data store by
   * its unique identifier or name.
   */
  COURSE_NOT_FOUND("error.domain.academic.course.not.found"),

  /**
   * Indicates an attempt to create or update an academic school using a name that is already
   * registered to another school in the system.
   */
  SCHOOL_ALREADY_EXISTS("error.domain.academic.school.already.exists"),

  /**
   * Indicates that a requested academic school could not be located in the underlying data store by
   * its unique identifier.
   */
  SCHOOL_NOT_FOUND("error.domain.academic.school.not.found"),

  /**
   * Indicates an attempt to enroll a student using an academic registration string that is already
   * assigned to an existing student.
   */
  STUDENT_ALREADY_EXISTS("error.domain.academic.student.already.exists"),

  /**
   * Indicates that a requested student enrollment record could not be located in the underlying
   * data store by its linked account ID, CPF, or academic registration.
   */
  STUDENT_NOT_FOUND("error.domain.academic.student.not.found");

  /** The property key used to resolve the localized error message in the resource bundles. */
  private final String bundleKey;

  /**
   * Constructs the {@code AcademicErrorCodes} enum.
   *
   * @param bundleKey the unique i18n key mapping to the application's resource bundles (e.g.,
   *     {@code messages_en_US.properties})
   */
  AcademicErrorCodes(String bundleKey) {
    this.bundleKey = bundleKey;
  }
}
