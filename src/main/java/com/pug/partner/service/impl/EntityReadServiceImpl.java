package com.pug.partner.service.impl;

import com.pug.partner.domain.enums.PartnerErrorCodes;
import com.pug.partner.infra.read.EntityQueries;
import com.pug.partner.infra.read.dtos.EntityView;
import com.pug.partner.service.EntityReadService;
import com.pug.shared.exceptions.ResourceNotFoundException;
import com.pug.shared.utils.StringUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

import java.util.List;
import java.util.UUID;

/**
 * Read-only service for entity views.
 */
@ApplicationScoped
public class EntityReadServiceImpl implements EntityReadService {

  private static final Logger LOG = Logger.getLogger(EntityReadServiceImpl.class);

  @Inject
  EntityQueries queries;

  @Override
  public EntityView getViewById(UUID id) {
    return queries
            .findOptionalById(id)
            .orElseThrow(() -> {
              LOG.debugf("Entity lookup failed: ID %s not found", id);
              return new ResourceNotFoundException(
                      PartnerErrorCodes.ENTITY_NOT_FOUND,
                      "id",
                      id.toString()
              );
            });
  }

  @Override
  public EntityView getViewByCnpj(String cnpj) {
    if (StringUtils.isEmpty(cnpj)) {
      throw new ResourceNotFoundException(
              PartnerErrorCodes.ENTITY_NOT_FOUND,
              "cnpj",
              "empty"
      );
    }

    return queries
            .findOptionalByCnpj(cnpj)
            .orElseThrow(() -> {
              LOG.debugf("Entity lookup failed: CNPJ %s not found", cnpj);
              return new ResourceNotFoundException(
                      PartnerErrorCodes.ENTITY_NOT_FOUND,
                      "cnpj",
                      cnpj
              );
            });
  }

  @Override
  public List<EntityView> listViews() {
    return queries.listAllEntities();
  }

  @Override
  public List<EntityView> listViewsByCityId(UUID cityId) {
    if (cityId == null) {
      return List.of();
    }
    return queries.listAllByCityId(cityId);
  }

  @Override
  public List<EntityView> searchViews(String query) {
    String key = StringUtils.fold(query);
    return queries.searchByName(key);
  }
}