package com.pug.project.domain.enums;

import com.pug.shared.domain.enums.GenericCodes;
import lombok.Getter;

/**
 * Enumeration representing the valid lifecycle states of a Student Enrollment.
 *
 * <p>Implements {@link GenericCodes} to allow the presentation layer to easily translate these
 * statuses into localized, human-readable strings.
 */
@Getter
public enum EnrollmentStatus implements GenericCodes {

  /** Indicates the student has been accepted into the project. */
  APPROVED("enrollment.status.approved"),

  /** Indicates the enrollment was canceled before the project concluded. */
  CANCELED("enrollment.status.canceled"),

  /** Indicates the student successfully completed the project requirements. */
  COMPLETED("enrollment.status.completed"),

  /** Indicates the student voluntarily withdrew from the project. */
  EXITED("enrollment.status.exited"),

  /** Indicates the enrollment request is awaiting staff review. */
  PENDING("enrollment.status.pending"),

  /** Indicates the enrollment request was denied by staff. */
  REJECTED("enrollment.status.rejected"),

  /** Indicates the student was administratively removed from the project. */
  REMOVED("enrollment.status.removed");

  private final String bundleKey;

  EnrollmentStatus(String bundleKey) {
    this.bundleKey = bundleKey;
  }
}
