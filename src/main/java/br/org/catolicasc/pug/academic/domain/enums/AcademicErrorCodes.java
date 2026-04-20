package br.org.catolicasc.pug.academic.domain.enums;

import br.org.catolicasc.pug.shared.domain.enums.GenericCodes;
import lombok.Getter;

/**
 * Enumeration of high-level domain error codes specific to the academic context.
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
   * Indicates an attempt to delete or modify an academic course that currently has active student
   * enrollments associated with it, violating relational integrity.
   */
  COURSE_HAS_STUDENTS("error.domain.academic.course.has.students"),

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
   * Indicates an attempt to delete or modify an academic school that currently has registered
   * courses under its hierarchy, violating relational integrity.
   */
  SCHOOL_HAS_COURSES("error.domain.academic.school.has.courses"),

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
   * Indicates an attempt to remove or alter a student profile that still retains active or
   * historical academic enrollments, violating relational integrity.
   */
  STUDENT_HAS_ENROLLMENTS("error.domain.academic.student.has.enrollments"),

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
