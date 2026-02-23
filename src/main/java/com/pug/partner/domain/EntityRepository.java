package com.pug.partner.domain;

import java.util.Optional;
import java.util.UUID;

/** Repository port for partner entities. Works with domain models. */
public interface EntityRepository {

  /**
   * Persists an Entity object.
   *
   * @param entity the Entity to persist.
   * @return the persisted Entity.
   */
  Entity persist(Entity entity);

  /**
   * Updates an existing Entity object.
   *
   * @param entity the Entity to update.
   */
  void update(Entity entity);

  /**
   * Deletes an Entity by its ID.
   *
   * @param id the UUID of the Entity to delete.
   * @return true if the Entity was successfully deleted, false if it was not found.
   */
  boolean deleteById(UUID id);

  /**
   * Finds an Entity by its ID.
   *
   * <p>Note: The returned Entity may contain validation errors (check {@code entityId.hasErrors()})
   * if the stored data is inconsistent with current domain rules.
   *
   * @param id the UUID of the Entity to find.
   * @return an Optional containing the found Entity, or empty if not found.
   */
  Optional<Entity> findOptionalById(UUID id);

  /**
   * Checks if an Entity exists by its CNPJ.
   *
   * @param cnpj the CNPJ to check.
   * @return true if an Entity with the given CNPJ exists, false otherwise.
   */
  boolean existsByCnpj(String cnpj);
}
