package com.pug.academic.service;

import com.pug.academic.domain.Student;
import com.pug.academic.service.dtos.StudentCreateCommand;
import com.pug.academic.service.dtos.StudentUpdateCommand;

import java.util.UUID;

/**
 * Interface for managing Student entities.
 */
public interface StudentService {

  /**
   * Saves a new Student entity.
   *
   * <p>This method also creates and saves the associated Account.
   *
   * @param cmd the command containing the data to create the new Student.
   * @return the saved Student entity.
   * @throws com.pug.shared.exceptions.DuplicateResourceException if a student with the same
   *                                                              academic registration already exists, or if an account with the given email already exists.
   * @throws com.pug.shared.exceptions.ResourceNotFoundException  if the associated course does not
   *                                                              exist.
   * @throws com.pug.shared.exceptions.AppValidationException     if input validation fails.
   */
  Student save(StudentCreateCommand cmd);

  /**
   * Updates an existing Student entity.
   *
   * <p>This method also updates the associated Account.
   *
   * @param accountId the UUID of the student's account to update.
   * @param cmd       the command containing the new data for the student.
   * @return the updated Student entity.
   * @throws com.pug.shared.exceptions.ResourceNotFoundException  if the student with the given
   *                                                              account ID does not exist, or if the new course does not exist.
   * @throws com.pug.shared.exceptions.DuplicateResourceException if a student with the new academic
   *                                                              registration already exists, or if an account with the new email already exists.
   * @throws com.pug.shared.exceptions.AppValidationException     if input validation fails.
   */
  Student update(UUID accountId, StudentUpdateCommand cmd);

  /***
   * Deletes a Student entity by its account ID.
   *
   * @param accountId the UUID of the student's account to delete.
   * @return true if the student was successfully deleted, false if the student was not found.
   */
  boolean delete(UUID accountId);

  /**
   * Retrieves a Student entity by its account ID.
   *
   * @param accountId the UUID of the student's account.
   * @return the Student entity.
   * @throws com.pug.shared.exceptions.ResourceNotFoundException if the student with the given
   *                                                             account ID does not exist (or data is corrupted in DB).
   * @throws com.pug.shared.exceptions.AppValidationException    if the student is found but its data
   *                                                             is corrupted in the database.
   */
  Student getById(UUID accountId);
}
