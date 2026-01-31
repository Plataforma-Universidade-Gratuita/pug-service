package com.pug.academic.service;

import com.pug.academic.domain.School;
import com.pug.academic.service.dtos.SchoolCreateCommand;
import com.pug.academic.service.dtos.SchoolUpdateCommand;
import com.pug.shared.domain.enums.DeleteKeys;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Interface for managing School entities.
 */
public interface ISchoolService {

  /**
   * Saves a new School entity.
   *
   * @param cmd the command containing the data to create the new School.
   * @return the saved School entity.
   * @throws com.pug.shared.exceptions.DuplicateResourceException if a school with the same name
   *                                                              already exists.
   * @throws com.pug.shared.exceptions.AppValidationException     if input validation fails.
   */
  School save(SchoolCreateCommand cmd);

  /**
   * Saves multiple new School entities.
   *
   * @param cmds an iterable of commands for school creation.
   * @return a list of saved School entities.
   * @throws com.pug.shared.exceptions.DuplicateResourceException if any school with the same name
   *                                                              already exists, or if there are duplicate names in the input commands.
   * @throws com.pug.shared.exceptions.AppValidationException     if input validation fails for any
   *                                                              school in the bulk.
   */
  List<School> saveAll(Iterable<SchoolCreateCommand> cmds);

  /**
   * Updates an existing School entity.
   *
   * @param id  the UUID of the school to update.
   * @param cmd the command containing the new data for the school.
   * @return the updated School entity.
   * @throws com.pug.shared.exceptions.ResourceNotFoundException  if the school with the given ID
   *                                                              does not exist.
   * @throws com.pug.shared.exceptions.DuplicateResourceException if a school with the same name
   *                                                              already exists.
   * @throws com.pug.shared.exceptions.AppValidationException     if input validation fails.
   */
  School update(UUID id, SchoolUpdateCommand cmd);

  /**
   * Deletes School entities by their IDs.
   *
   * @param ids an iterable of UUIDs of the schools to delete.
   * @return a map containing the number of deleted schools and related entities.
   * @throws com.pug.shared.exceptions.ReferencedEntityException if any school is still referenced
   *                                                             by other modules.
   */
  Map<DeleteKeys, Long> deleteAll(Iterable<UUID> ids);

  /**
   * Lists all School entities.
   *
   * @return a list of all School entities.
   * @throws com.pug.shared.exceptions.ResourceNotFoundException if no school is found (or data is
   *                                                             corrupted in DB).
   */
  List<School> listAll();

  /**
   * Retrieves a School entity by its ID.
   *
   * @param id the UUID of the school.
   * @return the School entity.
   * @throws com.pug.shared.exceptions.ResourceNotFoundException if the school with the given ID
   *                                                             does not exist (or data is corrupted in DB).
   */
  School getById(UUID id);

  /**
   * Retrieves a School entity by its name.
   *
   * @param name the name of the school.
   * @return the School entity.
   * @throws com.pug.shared.exceptions.ResourceNotFoundException if the school with the given name
   *                                                             does not exist (or data is corrupted in DB).
   */
  School getByName(String name);

  /**
   * Checks if a School entity exists by its name.
   *
   * @param name the name of the school.
   * @return true if a school with the given name exists, false otherwise.
   */
  boolean existsByName(String name);

  /**
   * Checks if any School entities exist by their names.
   *
   * @param names an iterable of school names.
   * @return true if any school with the given names exists, false otherwise.
   */
  boolean existsAnyByNameIn(Iterable<String> names);
}