package com.pug.academic.service;

import com.pug.academic.domain.School;
import com.pug.academic.service.dtos.SchoolCreateCommand;
import com.pug.academic.service.dtos.SchoolUpdateCommand;
import java.util.UUID;

/** Interface for managing School entities. */
public interface SchoolService {

  /**
   * Saves a new School entityId.
   *
   * @param cmd the command containing the data to create the new School.
   * @return the saved School entityId.
   * @throws com.pug.shared.exceptions.DuplicateResourceException if a school with the same name
   *     already exists.
   * @throws com.pug.shared.exceptions.AppValidationException if input validation fails.
   */
  School save(SchoolCreateCommand cmd);

  /**
   * Updates an existing School entityId.
   *
   * @param id the UUID of the school to update.
   * @param cmd the command containing the new data for the school.
   * @return the updated School entityId.
   * @throws com.pug.shared.exceptions.ResourceNotFoundException if the school with the given ID
   *     does not exist.
   * @throws com.pug.shared.exceptions.DuplicateResourceException if a school with the same name
   *     already exists.
   * @throws com.pug.shared.exceptions.AppValidationException if input validation fails.
   */
  School update(UUID id, SchoolUpdateCommand cmd);

  /**
   * Deletes a School entityId by its ID.
   *
   * @param id the UUID of the school to delete.
   * @return true if the school was successfully deleted, false if the school with the given ID does
   *     not exist.
   */
  boolean delete(UUID id);

  /**
   * Retrieves a School entityId by its ID.
   *
   * @param id the UUID of the school.
   * @return the School entityId.
   * @throws com.pug.shared.exceptions.ResourceNotFoundException if the school with the given ID
   *     does not exist (or data is corrupted in DB).
   */
  School getById(UUID id);
}
