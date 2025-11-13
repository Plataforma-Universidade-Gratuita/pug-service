package com.pug.partner.service;

import com.pug.partner.domain.enums.PartnerErrorCodes;
import com.pug.partner.infra.read.EntityQueries;
import com.pug.partner.infra.read.dtos.EntityView;
import com.pug.shared.exceptions.ResourceNotFoundException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/** Read-only service for entity views. */
@ApplicationScoped
public class EntityReadService {

  @Inject EntityQueries queries;

  /**
   * Retrieves an EntityView by its ID.
   *
   * @param id the UUID of the entity
   * @return the EntityView
   * @throws ResourceNotFoundException if the entity is not found
   */
  public EntityView getViewById(UUID id) {
    return queries
        .findOptionalById(id)
        .orElseThrow(
            () ->
                new ResourceNotFoundException(
                    PartnerErrorCodes.ENTITY_NOT_FOUND, Map.of("id", id)));
  }

  /**
   * Retrieves an EntityView by its CNPJ.
   *
   * @param cnpj the CNPJ of the entity
   * @return the EntityView
   * @throws ResourceNotFoundException if the entity is not found
   */
  public EntityView getViewByCnpj(String cnpj) {
    return queries
        .findOptionalByCnpj(cnpj)
        .orElseThrow(
            () ->
                new ResourceNotFoundException(
                    PartnerErrorCodes.ENTITY_NOT_FOUND, Map.of("cnpj", cnpj)));
  }

  /**
   * Lists all EntityViews.
   *
   * @return a list of EntityViews
   */
  public List<EntityView> listViews() {
    return queries.listAllEntities();
  }

  /**
   * Lists EntityViews by city ID.
   *
   * @param cityId the UUID of the city
   * @return a list of EntityViews in the specified city
   */
  public List<EntityView> listViewsByCityId(UUID cityId) {
    return queries.listAllByCityId(cityId);
  }

  /**
   * Searches for EntityViews by name.
   *
   * @param query the search query
   * @return a list of matching EntityViews
   */
  public List<EntityView> searchViews(String query) {
    return queries.searchByName(query);
  }
}
