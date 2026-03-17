package com.pug.project.service.utils;

import com.pug.academic.domain.Student;
import com.pug.project.domain.Attendance;
import com.pug.project.domain.Project;
import java.math.BigDecimal;
import java.util.UUID;

/**
 * Stateless utility class responsible for mapping raw DTO command data into pure {@link Attendance}
 * Domain Aggregates.
 */
public class AttendanceProcessor {

  /**
   * Processes raw creation inputs and constructs a new {@link Attendance} domain aggregate in a
   * WAITING state.
   *
   * @param project the fully reconstituted {@link Project} aggregate
   * @param student the fully reconstituted {@link Student} aggregate
   * @param duration the duration of time the student spent on the project
   * @return a fully instantiated {@link Attendance} domain aggregate, potentially containing errors
   */
  public static Attendance processCreateInput(
      Project project, Student student, BigDecimal duration) {
    return Attendance.factory(project, student, duration);
  }

  /**
   * Processes raw validation inputs and mutates an existing {@link Attendance} into a PRESENT
   * state.
   *
   * @param existing the current, reconstituted {@link Attendance} aggregate
   * @param validatorId the unique identifier of the staff account validating the attendance
   * @param latitude the geographic latitude recorded at validation time
   * @param longitude the geographic longitude recorded at validation time
   * @param qrValidationHash the cryptographic hash of the scanned QR code
   * @return a new {@link Attendance} domain aggregate reflecting the validated state
   */
  public static Attendance processValidationInput(
      Attendance existing,
      UUID validatorId,
      BigDecimal latitude,
      BigDecimal longitude,
      String qrValidationHash) {
    return existing.validateAttendance(validatorId, latitude, longitude, qrValidationHash);
  }
}
