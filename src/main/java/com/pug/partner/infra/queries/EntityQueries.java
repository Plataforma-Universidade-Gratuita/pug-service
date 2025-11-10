package com.pug.partner.infra.queries;

import com.pug.partner.presenter.dtos.EntityView;
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
  Optional<EntityView> findById(UUID id);

  /**
   * Lists all EntityView objects.
   *
   * @return a list of all EntityView objects.
   */
  List<EntityView> listAll();

  /**
   * Lists all EntityView objects by city ID.
   *
   * @param cityId the UUID of the city.
   * @return a list of EntityView objects in the specified city.
   */
  List<EntityView> listAllByCityId(UUID cityId);
}
