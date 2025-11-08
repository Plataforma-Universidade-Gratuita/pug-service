package com.pug.partner.domain;

import com.pug.partner.domain.vos.Cnpj;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** Repository port for partner entities. Works with domain models. */
public interface EntitiesRepository {

  /**
   * Persists an Entity object.
   *
   * @param entity the Entity to persist.
   */
  void persist(Entity entity);

  /**
   * Persists multiple Entity objects.
   *
   * @param entities the iterable collection of Entity objects to persist.
   */
  void persistAll(Iterable<Entity> entities);

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
  Optional<Entity> findOptionalByCnpj(Cnpj cnpj);

  /**
   * Lists all Entity objects.
   *
   * @return a list of all Entity objects.
   */
  List<Entity> listAllEntities();

  /**
   * Lists all Entity objects by city ID.
   *
   * @param cityId the ID of the city.
   * @return a list of Entity objects located in the specified city.
   */
  List<Entity> listAllByCityId(UUID cityId);

  /**
   * Searches for entities by name.
   *
   * @param query the name query string.
   * @return a list of entities matching the name query.
   */
  List<Entity> searchByName(String query);

  /**
   * Checks if an Entity exists by its CNPJ.
   *
   * @param cnpj the CNPJ to check.
   * @return true if an Entity with the given CNPJ exists, false otherwise.
   */
  boolean existsByCnpj(String cnpj);
}
