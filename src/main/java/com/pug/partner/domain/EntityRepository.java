package com.pug.partner.domain;

import java.util.List;
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
   * Deletes Entity objects by their IDs.
   *
   * @param ids the iterable collection of UUIDs representing the IDs of the Entity objects to
   *     delete.
   * @return the number of entities deleted.
   */
  long deleteByIds(Iterable<UUID> ids);

  /**
   * Finds an Entity by its ID.
   *
   * @param id the UUID of the Entity to find.
   * @return an Optional containing the found Entity, or empty if not found.
   */
  Optional<Entity> findOptionalById(UUID id);

  /**
   * Finds an Entity by its CNPJ.
   *
   * @param cnpj the CNPJ of the Entity to find.
   * @return an Optional containing the found Entity, or empty if not found.
   */
  Optional<Entity> findOptionalByCnpj(String cnpj);

  /**
   * Lists all Entity objects.
   *
   * @return a list of all Entity objects.
   */
  List<Entity> listAllEntities();

  /**
   * Checks if an Entity exists by its CNPJ.
   *
   * @param cnpj the CNPJ to check.
   * @return true if an Entity with the given CNPJ exists, false otherwise.
   */
  boolean existsByCnpj(String cnpj);

  /**
   * Checks if any Entity exists with a city ID in the provided list.
   *
   * @param cityIds the list of city IDs to check.
   * @return true if any Entity exists with a city ID in the list, false otherwise.
   */
  boolean existsAnyByCityIdIn(Iterable<UUID> cityIds);
}
