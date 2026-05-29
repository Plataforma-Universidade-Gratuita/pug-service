package br.org.catolicasc.pug.partner.service;

import br.org.catolicasc.pug.partner.domain.Entity;
import br.org.catolicasc.pug.partner.domain.Staff;
import br.org.catolicasc.pug.partner.service.dtos.EntityCreateCommand;
import br.org.catolicasc.pug.partner.service.dtos.EntityUpdateCommand;
import br.org.catolicasc.pug.shared.exceptions.AppValidationException;
import br.org.catolicasc.pug.shared.exceptions.DuplicateResourceException;
import br.org.catolicasc.pug.shared.exceptions.ResourceNotFoundException;
import java.util.UUID;

/**
 * Application service interface for managing the state of partner {@link Entity} domain
 * aggregates.
 *
 * <p>Following CQRS principles, this service handles the "Command" operations (Create, Update,
 * Delete) and strict domain-level retrievals. It enforces cross-cutting business rules (e.g.,
 * CNPJ uniqueness) and coordinates with the geo module to ensure geographical references are
 * valid.
 */
public interface EntitiesService {

  /**
   * Removes a partner {@link Entity} from the system by its unique identifier.
   *
   * <p>This operation enforces data hygiene. Before the partner entity is deleted, the service
   * cascades the deletion down to revoke all associated {@link Staff} privileges.
   *
   * @param id the unique identifier (UUID) of the partner entity to delete
   * @return {@code true} if the entity was successfully deleted, {@code false} if it was not found
   */
  boolean delete(UUID id);

  /**
   * Determines if any partner entities are currently associated with a specific city.
   *
   * @param cityId the unique identifier of the city to check
   * @return {@code true} if the city is referenced by any entity, {@code false} otherwise
   */
  boolean existsAnyByCityId(UUID cityId);

  /**
   * Retrieves a full partner {@link Entity} domain aggregate by its unique identifier.
   *
   * <p><b>Note:</b> This method is intended strictly for internal domain orchestration. For API
   * responses, use {@link EntitiesReadService#getViewById(UUID)} instead.
   *
   * @param id the unique identifier (UUID) of the partner entity
   * @return the fully reconstituted {@link Entity} aggregate
   * @throws ResourceNotFoundException if the entity does not exist
   * @throws AppValidationException if the entity exists but its stored state violates strict domain
   *     invariants
   */
  Entity getById(UUID id);

  /**
   * Instantiates and persists a new partner {@link Entity} aggregate based on the provided
   * command.
   *
   * @param cmd the structured command containing the data to create the new partner entity
   * @return the fully instantiated and persisted {@link Entity} aggregate
   * @throws DuplicateResourceException if an entity with the given CNPJ already exists
   * @throws AppValidationException if input validation fails
   * @throws ResourceNotFoundException if the referenced city ID does not exist
   */
  Entity save(EntityCreateCommand cmd);

  /**
   * Updates an existing partner {@link Entity} using the provided data.
   *
   * @param id the unique identifier (UUIDv7) of the partner entity to be updated
   * @param cmd the structured command containing the data to update the entity
   * @return the mutated and persisted {@link Entity} aggregate
   * @throws ResourceNotFoundException if the entity or referenced city does not exist
   * @throws AppValidationException if the updated input data violates domain constraints
   */
  Entity update(UUID id, EntityUpdateCommand cmd);
}
