package com.pug.projects.domain.enums;

import com.pug.shared.domain.enums.GenericCodes;
import lombok.Getter;

/**
 * Enumeration representing the valid lifecycle states of an Attendance record.
 * <p>
 * Implements {@link GenericCodes} to allow the presentation layer to easily
 * translate these statuses into localized, human-readable strings.
 */
@Getter
public enum AttendanceStatus implements GenericCodes {

  /**
   * Indicates the student was absent or the attendance was rejected.
   */
  ABSENT("attendance.status.absent"),

  /**
   * Indicates the attendance has been successfully validated and the student was present.
   */
  PRESENT("attendance.status.present"),

  /**
   * Indicates the attendance has been recorded but is pending staff validation.
   */
  WAITING("attendance.status.waiting");

  private final String bundleKey;

  AttendanceStatus(String bundleKey) {
    this.bundleKey = bundleKey;
  }
}