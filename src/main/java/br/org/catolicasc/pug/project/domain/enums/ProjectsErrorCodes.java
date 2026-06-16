package br.org.catolicasc.pug.project.domain.enums;

import br.org.catolicasc.pug.shared.domain.enums.GenericCodes;
import lombok.Getter;

/**
 * Enumeration of high-level domain error codes specific to the Projects context.
 *
 * <p>This enum implements {@link GenericCodes} to map business rule violations, invalid state
 * transitions, and resource state conflicts directly to localized messages.
 */
@Getter
public enum ProjectsErrorCodes implements GenericCodes {
  /** Indicates an attempt to delete a project that still has enrollments. */
  PROJECT_HAS_ENROLLMENTS("error.domain.projects.has.enrollments"),

  /** Indicates that a requested attendance record could not be found. */
  ATTENDANCE_NOT_FOUND("error.domain.projects.attendance.not.found"),

  /** Indicates an attempt to create an enrollment that already exists. */
  ENROLLMENT_ALREADY_EXISTS("error.domain.projects.enrollment.already.exists"),

  /** Indicates that a former student and project do not share a linked area of expertise. */
  ENROLLMENT_AREA_OF_EXPERTISE_MISMATCH(
      "error.domain.projects.enrollment.area.of.expertise.mismatch"),

  /** Indicates an attempt to create an enrollment for a project that no longer accepts them. */
  ENROLLMENT_PROJECT_UNAVAILABLE("error.domain.projects.enrollment.project.unavailable"),

  /** Indicates that a requested enrollment record could not be found. */
  ENROLLMENT_NOT_FOUND("error.domain.projects.enrollment.not.found"),

  /* --- State Transition Business Rules --- */

  /** Indicates an invalid state transition attempting on an enrollment. */
  INVALID_ENROLLMENT_STATUS_UPDATE("error.domain.projects.enrollment.status.invalid"),

  /** Indicates an invalid state transition attempting to cancel a project. */
  INVALID_PROJECT_STATUS_UPDATE_CANCEL("error.domain.projects.status.update.cancel"),

  /** Indicates an invalid state transition attempting to complete a project. */
  INVALID_PROJECT_STATUS_UPDATE_COMPLETE("error.domain.projects.status.update.complete"),

  /** Indicates an invalid state transition attempting to put a project on hold. */
  INVALID_PROJECT_STATUS_UPDATE_PUT_ON_HOLD("error.domain.projects.status.update.put.on.hold"),

  /** Indicates an invalid state transition attempting to resume a project. */
  INVALID_PROJECT_STATUS_UPDATE_RETAKE("error.domain.projects.status.update.retake"),

  /** Indicates an invalid state transition attempting to start a project. */
  INVALID_PROJECT_STATUS_UPDATE_START("error.domain.projects.status.update.start"),

  /* --- Resource Conflicts --- */

  /** Indicates an attempt to create a project with a name that is already in use. */
  PROJECT_ALREADY_EXISTS("error.domain.projects.already.exists"),

  /** Indicates that a requested project could not be found. */
  PROJECT_NOT_FOUND("error.domain.projects.not.found");

  private final String bundleKey;

  ProjectsErrorCodes(String bundleKey) {
    this.bundleKey = bundleKey;
  }
}
