package com.pug.partner.infra.read;

import com.pug.partner.domain.Entity;
import com.pug.partner.infra.read.dtos.EntityView;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** Queries related to Entities. */
public interface EntityQueries {
  /**
   * Finds an EntityView by its ID.
   *
   * @param id the UUID of the EntityView to find.
   * @return an Optional containing the found EntityView, or empty if not found.
   */
  Optional<EntityView> findOptionalById(UUID id);

  /**
   * Finds an EntityView by its CNPJ.
   *
   * @param cnpj the CNPJ of the EntityView to find.
   * @return an Optional containing the found EntityView, or empty if not found.
   */
  Optional<EntityView> findByCnpj(String cnpj);

  /**
   * Lists all EntityView objects.
   *
   * @return a list of all EntityView objects.
   */
  List<EntityView> listAllEntities();

  /**
   * Lists all EntityView objects by city ID.
   *
   * @param cityId the UUID of the city.
   * @return a list of EntityView objects in the specified city.
   */
  List<EntityView> listAllByCityId(UUID cityId);

  /**
   * Searches for entities by name.
   *
   * @param query the name query string.
   * @return a list of entities matching the name query.
   */
  List<EntityView> searchByName(String query);
}
