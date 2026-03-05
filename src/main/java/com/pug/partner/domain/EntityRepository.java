package com.pug.partner.domain;

import java.util.Optional;
import java.util.UUID;

/**
 * Domain repository interface for managing Partner {@link Entity} aggregate roots.
 *
 * <p>This interface defines the contract for persisting, retrieving, updating, and deleting partner
 * organization entities. It abstracts the underlying data storage mechanism to maintain a pure,
 * infrastructure-agnostic domain model.
 */
public interface EntityRepository {

  /**
   * Removes a Partner {@link Entity} from the repository based on its unique identifier.
   *
   * @param id the unique identifier (UUIDv7) of the partner entity to delete
   * @return {@code true} if the entity was successfully deleted, {@code false} if it was not found
   */
  boolean deleteById(UUID id);

  /**
   * Checks whether any Partner {@link Entity} exists that is associated with a specific city ID.
   *
   * <p>This is used to enforce referential integrity across bounded contexts, such as preventing
   * the deletion of a {@link com.pug.geo.domain.City} that is currently assigned to one or more
   * partner organizations.
   *
   * @param cityId the unique identifier of the city to check
   * @return {@code true} if at least one entity is located in the given city, {@code false}
   *     otherwise
   */
  boolean existsByCityId(UUID cityId);

  /**
   * Checks whether a Partner {@link Entity} with the specified CNPJ already exists in the
   * repository.
   *
   * <p>This is primarily used by domain services to enforce natural key uniqueness constraints
   * before persisting a new partner entity or updating an existing one's corporate ID.
   *
   * @param cnpj the raw numeric CNPJ string to check
   * @return {@code true} if an entity with the given CNPJ exists, {@code false} otherwise
   */
  boolean existsByCnpj(String cnpj);

  /**
   * Retrieves a Partner {@link Entity} by its unique identifier.
   *
   * <p>When an entity is reconstituted from the persistence layer, it typically undergoes the same
   * domain validations as a newly created aggregate. Therefore, the returned {@link Entity} might
   * contain validation errors (verifiable via {@link Entity#hasFieldErrors()}) if the stored data
   * violates current domain rules.
   *
   * @param id the unique identifier (UUID) of the partner entity
   * @return an {@link Optional} containing the {@link Entity} if found, or {@link Optional#empty()}
   *     if not
   */
  Optional<Entity> findOptionalById(UUID id);

  /**
   * Persists a newly created Partner {@link Entity} aggregate into the repository.
   *
   * @param entity the {@link Entity} aggregate to persist
   * @return the fully persisted {@link Entity} instance
   */
  Entity persist(Entity entity);

  /**
   * Updates the state of an existing Partner {@link Entity} aggregate in the repository.
   *
   * @param entity the {@link Entity} instance containing the updated state
   */
  void update(Entity entity);
}
