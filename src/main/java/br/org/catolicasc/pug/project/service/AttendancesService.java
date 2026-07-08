package br.org.catolicasc.pug.project.service;

import br.org.catolicasc.pug.project.domain.Attendance;
import br.org.catolicasc.pug.project.domain.vos.EnrollmentIdentifier;
import br.org.catolicasc.pug.project.service.dtos.attendance.AttendanceCreateCommand;
import br.org.catolicasc.pug.project.service.dtos.attendance.AttendanceValidateCommand;
import br.org.catolicasc.pug.shared.exceptions.AppValidationException;
import br.org.catolicasc.pug.shared.exceptions.BusinessRuleException;
import br.org.catolicasc.pug.shared.exceptions.ResourceNotFoundException;
import java.util.UUID;

/**
 * Application-layer command contract for attendance lifecycle operations.
 *
 * <p>This boundary centralizes attendance creation, validation, deletion, and bulk cleanup flows
 * triggered by enrollment or project lifecycle changes.
 */
public interface AttendancesService {

  /**
   * Removes all attendance records associated with a specific enrollment.
   *
   * @param identifier the composite identifier of the enrollment
   * @return the total number of records successfully deleted
   */
  long deleteAllByEnrollmentIdentifier(EnrollmentIdentifier identifier);

  /**
   * Removes only attendance records still awaiting validation for a specific enrollment.
   *
   * <p>This cleanup is used when an enrollment reaches a terminal lifecycle status. Validated
   * historical records, such as {@code PRESENT} and {@code ABSENT} attendances, must be preserved
   * so project and former-student completed-hour history remains auditable.
   *
   * @param identifier the composite identifier of the enrollment
   * @return the total number of waiting-validation attendance records deleted
   */
  long deleteAllWaitingValidationByEnrollmentIdentifier(EnrollmentIdentifier identifier);

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
   * @throws ResourceNotFoundException if no attendance exists for the provided identifier
   */
  Attendance getById(UUID id);

  /**
   * Instantiates and persists a new attendance entry.
   *
   * @param cmd the command containing attendance creation data
   * @return the persisted attendance aggregate
   * @throws BusinessRuleException if the referenced enrollment does not exist or cannot receive
   *     attendances
   * @throws AppValidationException if the requested attendance state violates domain constraints
   */
  Attendance save(AttendanceCreateCommand cmd);

  /**
   * Validates an attendance record, transitioning its state and applying staff authorization.
   *
   * @param id the unique identifier of the attendance
   * @param cmd the command containing validation status and hash
   * @return the updated attendance aggregate
   * @throws ResourceNotFoundException if the attendance does not exist or the submitted QR hash
   *     does not match the stored attendance
   * @throws BusinessRuleException if the requested validation violates an attendance or project
   *     business rule
   * @throws AppValidationException if the resulting attendance state violates domain constraints
   */
  Attendance validate(UUID id, AttendanceValidateCommand cmd);
}
