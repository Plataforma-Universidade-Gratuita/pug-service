package com.pug.projects.domain.enums;

import com.pug.shared.domain.enums.GenericErrorCodes;
import lombok.Getter;

/** Enumeration of error codes related to Projects domain. */
@Getter
public enum ProjectsErrorCodes implements GenericErrorCodes {
  INVALID_PROJECT_STATUS_UPDATE_CANCEL(
      "error.domain.projects.status.update.cancel", "projectStatus"),
  INVALID_PROJECT_STATUS_UPDATE_COMPLETE(
      "error.domain.projects.status.update.complete", "projectStatus"),
  INVALID_PROJECT_STATUS_UPDATE_PUT_ON_HOLD(
      "error.domain.projects.status.update.put.on.hold", "projectStatus"),
  INVALID_PROJECT_STATUS_UPDATE_RETAKE(
      "error.domain.projects.status.update.retake", "projectStatus"),
  INVALID_PROJECT_STATUS_UPDATE_START("error.domain.projects.status.update.start", "projectStatus"),
  INVALID_CREATED_AT_BLANK("error.domain.projects.createdat.blank", "createdAt"),
  INVALID_CREATED_AT_FUTURE("error.domain.projects.createdat.future", "createdAt"),
  INVALID_STATUS_BLANK("error.domain.projects.status.blank", "status"),
  INVALID_MAX_PARTICIPANTS_NEGATIVE(
      "error.domain.projects.maxparticipants.negative", "maxParticipants"),
  INVALID_ENROLLMENT_STUDENT_BLANK("error.domain.projects.enrollment.student.blank", "studentId"),
  INVALID_ENROLLMENT_PROJECT_BLANK("error.domain.projects.enrollment.project.blank", "projectId"),
  INVALID_ENROLLMENT_STATUS_BLANK("error.domain.projects.enrollment.status.blank", "status"),
  INVALID_ENROLLMENT_REQUEST_AT_BLANK(
      "error.domain.projects.enrollment.requestat.blank", "requestAt"),
  INVALID_ENROLLMENT_DATES_INVALID("error.domain.projects.enrollment.dates.invalid", "acceptedAt"),

  INVALID_ATTENDANCE_PROJECT_BLANK("error.domain.projects.attendance.project.blank", "projectId"),
  INVALID_ATTENDANCE_STUDENT_BLANK("error.domain.projects.attendance.student.blank", "studentId"),
  INVALID_ATTENDANCE_DURATION_INVALID(
      "error.domain.projects.attendance.duration.invalid", "duration"),
  INVALID_ATTENDANCE_GEO_INVALID_MISSING(
      "error.domain.projects.attendance.geo.invalid.missing", null),
  INVALID_ATTENDANCE_GEO_INVALID_LAT(
      "error.domain.projects.attendance.geo.invalid.lat", "latitude"),
  INVALID_ATTENDANCE_GEO_INVALID_LONG(
      "error.domain.projects.attendance.geo.invalid.long", "longitude"),
  INVALID_ATTENDANCE_STATUS_BLANK("error.domain.projects.attendance.status.blank", "status");

  private final String bundleKey;
  private final String fieldName;

  ProjectsErrorCodes(String bundleKey, String fieldName) {
    this.bundleKey = bundleKey;
    this.fieldName = fieldName;
  }
}
