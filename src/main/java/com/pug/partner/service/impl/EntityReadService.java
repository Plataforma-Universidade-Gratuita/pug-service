package com.pug.partner.service.impl;

import com.pug.partner.domain.enums.PartnerErrorCodes;
import com.pug.partner.infra.read.IEntityQueries;
import com.pug.partner.infra.read.dtos.EntityView;
import com.pug.partner.service.IEntityReadService;
import com.pug.shared.exceptions.ResourceNotFoundException;
import com.pug.shared.utils.StringUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Read-only service for entity views.
 */
@ApplicationScoped
public class EntityReadService implements IEntityReadService {

  @Inject
  IEntityQueries queries;

  @Override
  public EntityView getViewById(UUID id) {
    return queries
            .findOptionalById(id)
            .orElseThrow(
                    () ->
                            new ResourceNotFoundException(
                                    PartnerErrorCodes.ENTITY_NOT_FOUND, Map.of("id", id)));
  }

  @Override
  public EntityView getViewByCnpj(String cnpj) {
    if (StringUtils.isEmpty(cnpj)) {
      throw new ResourceNotFoundException(PartnerErrorCodes.ENTITY_NOT_FOUND, Map.of("cnpj", cnpj));
    }
    return queries
            .findOptionalByCnpj(cnpj)
            .orElseThrow(
                    () ->
                            new ResourceNotFoundException(
                                    PartnerErrorCodes.ENTITY_NOT_FOUND, Map.of("cnpj", cnpj)));
  }

  @Override
  public List<EntityView> listViews() {
    return queries.listAllEntities();
  }

  @Override
  public List<EntityView> listViewsByCityId(UUID cityId) {
    return queries.listAllByCityId(cityId);
  }

  @Override
  public List<EntityView> searchViews(String query) {
    String key = StringUtils.fold(query);
    return queries.searchByName(key);
  }
}