package br.org.catolicasc.pug.project.service.utils;

import br.org.catolicasc.pug.academic.domain.Student;
import br.org.catolicasc.pug.project.domain.Attendance;
import br.org.catolicasc.pug.project.domain.Project;
import br.org.catolicasc.pug.project.domain.enums.AttendanceStatus;
import java.math.BigDecimal;
import java.util.UUID;

/**
 * Stateless utility class responsible for mapping raw DTO command data into pure {@link Attendance}
 * Domain Aggregates.
 *
 * <p>This processor centralizes the orchestration of domain factory methods and state-mutation
 * behaviors, ensuring that the application service layer remains focused on coordination and error
 * handling.
 */
public final class AttendanceProcessor {

  /** Private constructor to prevent instantiation. */
  private AttendanceProcessor() {}

  /**
   * Processes raw creation inputs and constructs a new {@link Attendance} domain aggregate in a
   * WAITING state.
   *
   * @param project the fully reconstituted {@link Project} aggregate
   * @param student the fully reconstituted {@link Student} aggregate
   * @param duration the duration of time the student spent on the project
   * @param qrHash the unique cryptographic hash for the new QR code
   * @return a fully instantiated {@link Attendance} domain aggregate, potentially containing errors
   */
  public static Attendance processCreateInput(
      Project project, Student student, BigDecimal duration, String qrHash) {
    return Attendance.factory(project, student, duration, qrHash);
  }

  /**
   * Processes raw validation inputs and mutates an existing {@link Attendance} into a new status
   * (e.g., PRESENT or ABSENT).
   *
   * @param existing the current, reconstituted {@link Attendance} aggregate
   * @param validatorId the unique identifier of the staff account validating the attendance
   * @param status the new status to apply to the attendance record
   * @return a new {@link Attendance} domain aggregate reflecting the validated state
   */
  public static Attendance processValidationInput(
      Attendance existing, UUID validatorId, AttendanceStatus status) {
    return existing.validatePresence(validatorId, status);
  }
}
