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
     * Indicates an attempt to create or update an academic area of expertise using a name that is already
     * registered to another area of expertise in the system.
     */
    AREA_OF_EXPERTISE_ALREADY_EXISTS("error.domain.academic.areaOfExpertise.already.exists"),

    /**
     * Indicates an attempt to delete or modify an academic area of expertise that currently has registered
     * courses under its hierarchy, violating relational integrity.
     */
    AREA_OF_EXPERTISE_HAS_COURSES("error.domain.academic.areaOfExpertise.has.courses"),

    /**
     * Indicates that a requested academic area of expertise could not be located in the underlying data store by
     * its unique identifier.
     */
    AREA_OF_EXPERTISE_NOT_FOUND("error.domain.academic.areaOfExpertise.not.found"),

  /**
   * Indicates an attempt to create or update an academic course using a name that is already
   * registered in the system.
   */
  COURSE_ALREADY_EXISTS("error.domain.academic.course.already.exists"),

  /**
   * Indicates an attempt to delete or modify an academic course that currently has active
   * formerStudent enrollments associated with it, violating relational integrity.
   */
  COURSE_HAS_FORMER_STUDENTS("error.domain.academic.course.has.formerStudents"),

  /**
   * Indicates that a requested academic course could not be located in the underlying data store by
   * its unique identifier or name.
   */
  COURSE_NOT_FOUND("error.domain.academic.course.not.found"),

  /**
   * Indicates an attempt to enroll a formerStudent using an academic registration string that is
   * already assigned to an existing formerStudent.
   */
  FORMER_STUDENT_ALREADY_EXISTS("error.domain.academic.formerStudent.already.exists"),

  /**
   * Indicates an attempt to remove or alter a formerStudent profile that still retains active or
   * historical academic enrollments, violating relational integrity.
   */
  FORMER_STUDENT_HAS_ENROLLMENTS("error.domain.academic.formerStudent.has.enrollments"),

  /**
   * Indicates that a requested formerStudent enrollment record could not be located in the
   * underlying data store by its linked account ID, CPF, or academic registration.
   */
  FORMER_STUDENT_NOT_FOUND("error.domain.academic.formerStudent.not.found");

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
