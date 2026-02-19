package com.pug.projects.domain.enums;

import com.pug.shared.domain.enums.GenericErrorCodes;
import lombok.Getter;

/** Enumeration of error codes related to Projects domain. */
@Getter
public enum ProjectsErrorCodes implements GenericErrorCodes {
  INVALID_PROJECT_ID_BLANK("error.domain.projects.id.blank", "id"),
  INVALID_PROJECT_NAME_BLANK("error.domain.projects.name.blank", "name"),
  INVALID_PROJECT_NAME_LENGTH("error.domain.projects.name.toolong", "name"),
  INVALID_ENTITY_ID_BLANK("error.domain.projects.entity.blank", "entityId"),
  INVALID_DESCRIPTION_BLANK("error.domain.projects.description.blank", "description"),
  INVALID_DESCRIPTION_LENGTH("error.domain.projects.description.toolong", "description"),
  INVALID_CREATED_BY_BLANK("error.domain.projects.createdby.blank", "createdBy"),
  INVALID_CREATED_AT_BLANK("error.domain.projects.createdat.blank", "createdAt"),
  INVALID_CREATED_AT_FUTURE("error.domain.projects.createdat.future", "createdAt"),
  INVALID_OFFERED_HOURS_NEGATIVE("error.domain.projects.offeredhours.negative", "offeredHours"),
  INVALID_COMPLETED_HOURS_NEGATIVE(
      "error.domain.projects.completedhours.negative", "completedHours"),
  INVALID_COMPLETED_HOURS_EXCEEDS("error.domain.projects.completedhours.exceeds", "completedHours"),
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
  INVALID_ATTENDANCE_STATUS_BLANK("error.domain.projects.attendance.status.blank", "status"),

  PROJECT_NOT_FOUND("error.domain.projects.notfound", null),
  PROJECT_ALREADY_EXISTS("error.domain.projects.alreadyexists", null),
  ENROLLMENT_NOT_FOUND("error.domain.projects.enrollment.notfound", null),
  ENROLLMENT_ALREADY_EXISTS("error.domain.projects.enrollment.alreadyexists", null),
  ATTENDANCE_NOT_FOUND("error.domain.projects.attendance.notfound", null);

  private final String bundleKey;
  private final String fieldName;

  ProjectsErrorCodes(String bundleKey, String fieldName) {
    this.bundleKey = bundleKey;
    this.fieldName = fieldName;
  }
}
