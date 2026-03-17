package com.pug.project.service;

import com.pug.project.domain.Attendance;
import com.pug.project.service.dtos.AttendanceCreateCommand;
import com.pug.project.service.dtos.AttendanceValidateCommand;
import java.util.UUID;

/** Application service interface for managing the state of {@link Attendance} domain aggregates. */
public interface AttendanceService {

  /**
   * Removes an {@link Attendance} record from the system.
   *
   * @param id the unique identifier (UUID) of the attendance
   * @return {@code true} if deleted, {@code false} if not found
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
   * Retrieves a full {@link Attendance} aggregate by its identifier.
   *
   * @param id the unique identifier (UUID) of the attendance
   * @return the {@link Attendance} aggregate
   */
  Attendance getById(UUID id);

  /**
   * Instantiates and persists a new {@link Attendance} entry.
   *
   * @param cmd the command containing attendance creation data
   * @return the persisted {@link Attendance}
   */
  Attendance save(AttendanceCreateCommand cmd);

  /**
   * Validates an attendance record, verifying geographic data and applying staff authorization.
   *
   * @param id the unique identifier (UUID) of the attendance
   * @param cmd the command containing validation metrics and staff identifier
   * @return the updated {@link Attendance}
   */
  Attendance validate(UUID id, AttendanceValidateCommand cmd);
}
