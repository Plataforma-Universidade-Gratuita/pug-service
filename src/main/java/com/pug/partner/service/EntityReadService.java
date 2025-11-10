package com.pug.partner.service;

import com.pug.partner.infra.queries.EntityQueries;
import com.pug.partner.presenter.dtos.EntityView;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;
import java.util.UUID;

/** Read-only service for entity views. */
@ApplicationScoped
public class EntityReadService {

  @Inject EntityQueries queries;

  /**
   * Retrieves an EntityView by its unique identifier.
   *
   * @param id the unique identifier of the entity view.
   * @return the EntityView if found, otherwise null.
   */
  public EntityView getView(UUID id) {
    return queries.findById(id).orElse(null);
  }

  /**
   * Lists all EntityViews.
   *
   * @return a list of all EntityViews.
   */
  public List<EntityView> listViews() {
    return queries.listAll();
  }

  /**
   * Lists all EntityViews by city ID.
   *
   * @param cityId the unique identifier of the city.
   * @return a list of EntityViews associated with the specified city ID.
   */
  public List<EntityView> listViewsByCityId(UUID cityId) {
    return queries.listAllByCityId(cityId);
  }
}
