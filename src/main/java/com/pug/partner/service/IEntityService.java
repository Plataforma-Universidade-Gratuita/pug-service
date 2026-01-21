package com.pug.partner.service;

import com.pug.partner.domain.Entity;
import com.pug.partner.domain.vos.Cnpj;
import com.pug.partner.service.dtos.EntityCreateOrUpdateCommand;
import com.pug.shared.domain.enums.DeleteKeys;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/** Interface for managing partner entities. */
public interface IEntityService {

  /**
   * Saves a new Entity.
   *
   * @param cmd the command containing the data to create the Entity
   * @return the saved Entity
   * @throws com.pug.shared.exceptions.DuplicateResourceException if an entity with the same CNPJ
   *     already exists
   * @throws com.pug.shared.exceptions.AppValidationException if input validation fails (e.g., blank
   *     name, invalid CNPJ).
   * @throws com.pug.shared.exceptions.ResourceNotFoundException if the city with the given IBGE
   *     code does not exist.
   */
  Entity save(EntityCreateOrUpdateCommand cmd);

  /**
   * Updates an existing Entity.
   *
   * @param id the UUID of the Entity to update
   * @param cmd the command containing the updated data for the Entity
   * @throws com.pug.shared.exceptions.ResourceNotFoundException if the Entity is not found (or data
   *     corrupted) or city is not found.
   * @throws com.pug.shared.exceptions.DuplicateResourceException if an entity with the same CNPJ
   *     already exists.
   * @throws com.pug.shared.exceptions.AppValidationException if input validation fails.
   */
  Entity update(UUID id, EntityCreateOrUpdateCommand cmd);

  /**
   * Deletes Entities by their IDs.
   *
   * <p>Also deletes associated staff members and their underlying users.
   *
   * @param ids the UUIDs of the Entities to delete
   * @return a map containing the count of deleted entities, staff and accounts
   * @throws com.pug.shared.exceptions.ReferencedEntityException if any entity is still referenced
   *     (e.g., by staff members).
   */
  Map<DeleteKeys, Long> deleteAll(Iterable<UUID> ids);

  /**
   * Gets an Entity by its ID.
   *
   * @param id the UUID of the Entity
   * @return the Entity with the specified ID
   * @throws com.pug.shared.exceptions.ResourceNotFoundException if the Entity is not found (or data
   *     is corrupted in DB).
   */
  Entity getById(UUID id);

  /**
   * Gets an Entity by its CNPJ.
   *
   * @param cnpj the CNPJ of the Entity (already a validated Value Object).
   * @return the Entity with the specified CNPJ
   * @throws com.pug.shared.exceptions.ResourceNotFoundException if the Entity is not found (or data
   *     is corrupted in DB).
   */
  Entity getByCnpj(Cnpj cnpj);

  /**
   * Lists all Entities.
   *
   * @return a list of all Entities
   * @throws com.pug.shared.exceptions.AppValidationException if any Entity entity found is
   *     corrupted in the database.
   */
  List<Entity> listAll();

  /**
   * Checks if an Entity exists by its CNPJ.
   *
   * @param cnpj the CNPJ to check (already a validated Value Object).
   * @return true if an Entity with the given CNPJ exists, false otherwise.
   */
  boolean existsByCnpj(Cnpj cnpj);

  /**
   * Checks if any Entity exists in the given city IDs.
   *
   * @param cityIds the iterable of city UUIDs
   * @return true if any Entity exists in the specified cities, false otherwise
   */
  boolean existsAnyByCityIdIn(Iterable<UUID> cityIds);
}
