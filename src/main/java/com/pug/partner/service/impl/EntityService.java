package com.pug.partner.service.impl;

import com.pug.geo.service.ICityService;
import com.pug.partner.domain.Entity;
import com.pug.partner.domain.IEntityRepository;
import com.pug.partner.domain.enums.PartnerErrorCodes;
import com.pug.partner.service.EntityProcessor;
import com.pug.partner.service.IEntityService;
import com.pug.partner.service.IStaffService;
import com.pug.partner.service.dtos.EntityCreateCommand;
import com.pug.partner.service.dtos.EntityUpdateCommand;
import com.pug.shared.domain.enums.DeleteKeys;
import com.pug.shared.exceptions.AppValidationException;
import com.pug.shared.exceptions.DuplicateResourceException;
import com.pug.shared.exceptions.ReferencedEntityException;
import com.pug.shared.exceptions.ResourceNotFoundException;
import com.pug.shared.utils.CollectionUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.jboss.logging.Logger;

@ApplicationScoped
public class EntityService implements IEntityService {

  private static final Logger LOG = Logger.getLogger(EntityService.class);

  @Inject IEntityRepository repo;
  @Inject ICityService cityService;
  @Inject IStaffService staffService;

  @Transactional
  @Override
  public Entity save(EntityCreateCommand cmd) {
    if (cmd.cityId() != null) {
      cityService.getById(cmd.cityId());
    }

    Entity entityToPersist =
        EntityProcessor.processCreateInput(
            cmd.cnpjString(), cmd.name(), cmd.cityId(), cmd.address());

    if (entityToPersist.hasErrors()) {
      throw new AppValidationException(entityToPersist.getProblems());
    }

    if (existsByCnpj(entityToPersist.getCnpj())) {
      throw new DuplicateResourceException(
          PartnerErrorCodes.ENTITY_ALREADY_EXISTS,
          Map.of("cnpj", entityToPersist.getCnpj().toString()));
    }

    return repo.persist(entityToPersist);
  }

  @Transactional
  @Override
  public Entity update(UUID id, EntityUpdateCommand cmd) {
    Entity current = getById(id);

    if (cmd.cityId() != null) {
      cityService.getById(cmd.cityId());
    }

    Entity updatedEntity =
        EntityProcessor.processUpdateInput(
            current, cmd.cnpjString(), cmd.name(), cmd.cityId(), cmd.address());

    if (updatedEntity.hasErrors()) {
      throw new AppValidationException(updatedEntity.getProblems());
    }

    if (!updatedEntity.getCnpj().equals(current.getCnpj())
        && existsByCnpj(updatedEntity.getCnpj())) {
      throw new DuplicateResourceException(
          PartnerErrorCodes.ENTITY_ALREADY_EXISTS,
          Map.of("cnpj", updatedEntity.getCnpj().toString()));
    }

    repo.update(updatedEntity);
    return getById(id);
  }

  @Transactional
  @Override
  public Map<DeleteKeys, Long> deleteAll(Iterable<UUID> ids) {
    if (CollectionUtils.isEmpty(ids)) {
      return Map.of();
    }

    for (UUID entityId : ids) {
      if (!staffService.listByEntity(entityId).isEmpty()) {
        throw new ReferencedEntityException(
            PartnerErrorCodes.ENTITY_STILL_REFERENCED, Map.of("entityId", entityId));
      }
    }

    long entitiesDeleted = repo.deleteByIds(ids);

    return Map.of(
        DeleteKeys.ENTITIES, entitiesDeleted,
        DeleteKeys.STAFF, 0L,
        DeleteKeys.ACCOUNTS, 0L,
        DeleteKeys.USERS, 0L);
  }

  @Override
  public Entity getById(UUID id) {
    Entity entity =
        repo.findOptionalById(id)
            .orElseThrow(
                () ->
                    new ResourceNotFoundException(
                        PartnerErrorCodes.ENTITY_NOT_FOUND, Map.of("id", id)));

    if (entity.hasErrors()) {
      LOG.errorf(
          "Data integrity error: Entity with ID %s in DB violates domain rules. Problems: %s",
          id, entity.getProblemsSummary());
      throw new ResourceNotFoundException(PartnerErrorCodes.ENTITY_NOT_FOUND, Map.of("id", id));
    }

    return entity;
  }

  @Override
  public Entity getByCnpj(String cnpjString) {
    Entity entity =
        repo.findOptionalByCnpj(cnpjString)
            .orElseThrow(
                () ->
                    new ResourceNotFoundException(
                        PartnerErrorCodes.ENTITY_NOT_FOUND, Map.of("cnpj", cnpjString)));

    if (entity.hasErrors()) {
      LOG.errorf(
          "Data integrity error: Entity with CNPJ %s in DB violates domain rules. Problems: %s",
          cnpjString, entity.getProblemsSummary());
      throw new ResourceNotFoundException(
          PartnerErrorCodes.ENTITY_NOT_FOUND, Map.of("cnpj", cnpjString));
    }

    return entity;
  }

  @Override
  public List<Entity> listAll() {
    List<Entity> entities = repo.listAllEntities();
    for (Entity entity : entities) {
      if (entity.hasErrors()) {
        LOG.errorf(
            "Data integrity error: Corrupted Entity entity found in DB. ID: %s. Problems: %s",
            entity.getId(), entity.getProblemsSummary());
        throw new ResourceNotFoundException(PartnerErrorCodes.ENTITY_NOT_FOUND);
      }
    }
    return entities;
  }

  @Override
  public boolean existsByCnpj(com.pug.partner.domain.vos.Cnpj cnpj) {
    if (cnpj == null) {
      return false;
    }
    return repo.existsByCnpj(cnpj.toString());
  }

  @Override
  public boolean existsAnyByCityIdIn(Iterable<UUID> cityIds) {
    if (CollectionUtils.isEmpty(cityIds)) {
      return false;
    }
    return repo.existsAnyByCityIdIn(cityIds);
  }
}
