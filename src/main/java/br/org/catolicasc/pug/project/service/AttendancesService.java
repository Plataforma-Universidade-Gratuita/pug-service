package br.org.catolicasc.pug.project.service;

import br.org.catolicasc.pug.project.domain.Attendance;
import br.org.catolicasc.pug.project.domain.vos.EnrollmentIdentifier;
import br.org.catolicasc.pug.project.service.dtos.attendance.AttendanceCreateCommand;
import br.org.catolicasc.pug.project.service.dtos.attendance.AttendanceValidateCommand;
import java.util.UUID;

/** Application service interface for managing attendance aggregates. */
public interface AttendancesService {

  /**
   * Removes all attendance records associated with a specific enrollment.
   *
   * @param identifier the composite identifier of the enrollment
   * @return the total number of records successfully deleted
   */
  long deleteAllByEnrollmentIdentifier(EnrollmentIdentifier identifier);

  /**
   * Removes an attendance record from the system.
   *
   * @param id the unique identifier of the attendance
   * @return {@code true} when the record was deleted, {@code false} otherwise
   */
  boolean delete(UUID id);

  /**
   * Checks if any attendance records were validated by a specific staff account.
   *
   * @param accountId the unique identifier of the staff account
   * @return {@code true} if any records exist
   */
  boolean existsByValidatedBy(UUID accountId);

  /**
   * Retrieves a full attendance aggregate by its identifier.
   *
   * @param id the unique identifier of the attendance
   * @return the matching attendance aggregate
   */
  Attendance getById(UUID id);

  /**
   * Instantiates and persists a new attendance entry.
   *
   * @param cmd the command containing attendance creation data
   * @return the persisted attendance aggregate
   */
  Attendance save(AttendanceCreateCommand cmd);

  /**
   * Validates an attendance record, transitioning its state and applying staff authorization.
   *
   * @param id the unique identifier of the attendance
   * @param cmd the command containing validation status and hash
   * @return the updated attendance aggregate
   */
  Attendance validate(UUID id, AttendanceValidateCommand cmd);
}
