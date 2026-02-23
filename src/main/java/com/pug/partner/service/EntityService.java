package com.pug.partner.service;

import com.pug.partner.domain.Entity;
import com.pug.partner.service.dtos.EntityCreateCommand;
import com.pug.partner.service.dtos.EntityUpdateCommand;

import java.util.List;
import java.util.UUID;

/**
 * Interface for managing partner entities.
 */
public interface EntityService {

  /**
   * Saves a new Entity.
   *
   * @param cmd the command containing the data to create the Entity
   * @return the saved Entity
   * @throws com.pug.shared.exceptions.DuplicateResourceException if an entity with the same CNPJ
   *                                                              already exists
   * @throws com.pug.shared.exceptions.AppValidationException     if input validation fails (e.g., blank
   *                                                              name, invalid CNPJ).
   */
  Entity save(EntityCreateCommand cmd);

  /**
   * Updates an existing Entity.
   *
   * @param id  the UUID of the Entity to update
   * @param cmd the command containing the updated data for the Entity
   * @return the updated Entity
   * @throws com.pug.shared.exceptions.ResourceNotFoundException  if the Entity is not found (or data
   *                                                              corrupted) or city is not found.
   * @throws com.pug.shared.exceptions.DuplicateResourceException if an entity with the same CNPJ
   *                                                              already exists.
   * @throws com.pug.shared.exceptions.AppValidationException     if input validation fails.
   */
  Entity update(UUID id, EntityUpdateCommand cmd);

  /**
   * Deletes an Entity by its ID.
   *
   * @param id the UUID of the Entity to delete
   * @return true if the Entity was successfully deleted, false if the Entity was not found or is
   * still referenced by any Staff.
   */
  boolean delete(UUID id);

  /**
   * Gets an Entity by its ID.
   *
   * @param id the UUID of the Entity
   * @return the Entity with the specified ID
   * @throws com.pug.shared.exceptions.ResourceNotFoundException if the Entity is not found (or data
   *                                                             is corrupted in DB).
   */
  Entity getById(UUID id);

  /**
   * Lists all Entities.
   *
   * @return a list of all Entities
   * @throws com.pug.shared.exceptions.ResourceNotFoundException if any Entity entity found is
   *                                                             corrupted in the database.
   */
  List<Entity> listAll();
}
