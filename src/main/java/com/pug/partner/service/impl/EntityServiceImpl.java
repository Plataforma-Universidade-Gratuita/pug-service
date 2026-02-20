package com.pug.partner.service.impl;

import com.pug.geo.service.CityService;
import com.pug.partner.domain.Entity;
import com.pug.partner.domain.EntityRepository;
import com.pug.partner.domain.enums.PartnerErrorCodes;
import com.pug.partner.domain.vos.Cnpj;
import com.pug.partner.service.EntityService;
import com.pug.partner.service.StaffService;
import com.pug.partner.service.dtos.EntityCreateCommand;
import com.pug.partner.service.dtos.EntityUpdateCommand;
import com.pug.partner.service.utils.EntityProcessor;
import com.pug.shared.exceptions.AppValidationException;
import com.pug.shared.exceptions.DuplicateResourceException;
import com.pug.shared.exceptions.ResourceNotFoundException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;

import java.util.List;
import java.util.UUID;

/**
 * Service class for managing Entity entities.
 */
@ApplicationScoped
public class EntityServiceImpl implements EntityService {

  private static final Logger LOG = Logger.getLogger(EntityServiceImpl.class);

  @Inject
  EntityRepository repo;

  @Inject
  CityService cityService;

  @Inject
  StaffService staffService;

  @Transactional
  @Override
  public Entity save(EntityCreateCommand cmd) {
    LOG.debugf("Attempting to create Entity: %s (CNPJ: %s)", cmd.name(), cmd.cnpjString());
    if (cmd.cityId() != null) {
      cityService.getById(cmd.cityId());
    }

    Entity entityToPersist = EntityProcessor.processCreateInput(
            cmd.cnpjString(),
            cmd.name(),
            cmd.cityId(),
            cmd.address()
    );

    if (entityToPersist.hasErrors()) {
      throw new AppValidationException(entityToPersist.getProblems());
    }

    if (existsByCnpj(entityToPersist.getCnpj())) {
      LOG.warnf("Creation failed: Entity with CNPJ %s already exists", entityToPersist.getCnpj());
      throw new DuplicateResourceException(
              PartnerErrorCodes.ENTITY_ALREADY_EXISTS,
              "cnpj",
              entityToPersist.getCnpj().toString()
      );
    }

    Entity savedEntity = repo.persist(entityToPersist);
    LOG.infof("Entity created successfully. ID: %s", savedEntity.getId());
    return savedEntity;
  }

  @Transactional
  @Override
  public Entity update(UUID id, EntityUpdateCommand cmd) {
    LOG.debugf("Attempting to update Entity ID: %s", id);
    Entity current = getById(id);
    if (cmd.cityId() != null) {
      cityService.getById(cmd.cityId());
    }

    Entity updatedEntity = EntityProcessor.processUpdateInput(
            current,
            cmd.cnpjString(),
            cmd.name(),
            cmd.cityId(),
            cmd.address()
    );

    if (updatedEntity.hasErrors()) {
      throw new AppValidationException(updatedEntity.getProblems());
    }

    if (!updatedEntity.getCnpj().equals(current.getCnpj())
            && existsByCnpj(updatedEntity.getCnpj())) {
      LOG.warnf("Update failed: Entity ID %s tried to use existing CNPJ %s", id, updatedEntity.getCnpj());
      throw new DuplicateResourceException(
              PartnerErrorCodes.ENTITY_ALREADY_EXISTS,
              "cnpj",
              updatedEntity.getCnpj().toString()
      );
    }

    repo.update(updatedEntity);
    LOG.infof("Entity updated successfully. ID: %s", id);
    return getById(id);
  }

  @Transactional
  @Override
  public boolean delete(UUID id) {
    LOG.debugf("Attempting to delete Entity ID: %s", id);
    if (id == null) {
      return false;
    }

    staffService.deleteAllByEntityId(id);
    boolean deleted = repo.deleteById(id);
    if (deleted) {
      LOG.infof("Entity deleted successfully. ID: %s", id);
    } else {
      LOG.debugf("Delete failed: Entity ID %s not found (idempotent)", id);
    }

    return deleted;
  }

  @Override
  public Entity getById(UUID id) {
    Entity entity = repo.findOptionalById(id)
            .orElseThrow(() -> {
              LOG.debugf("Entity lookup failed: ID %s not found", id);
              return new ResourceNotFoundException(
                      PartnerErrorCodes.ENTITY_NOT_FOUND,
                      "id",
                      id.toString()
              );
            });

    if (entity.hasErrors()) {
      LOG.errorf("DATA CORRUPTION DETECTED: Entity %s violates domain rules: %s",
              id, entity.getProblemsSummary());
      throw new ResourceNotFoundException(
              PartnerErrorCodes.ENTITY_NOT_FOUND,
              "id",
              id.toString()
      );
    }

    return entity;
  }

  @Override
  public List<Entity> listAll() {
    LOG.debug("Listing all entities");
    List<Entity> entities = repo.listAllEntities();

    return entities.stream()
            .filter(entity -> {
              if (entity.hasErrors()) {
                LOG.errorf("DATA CORRUPTION DETECTED: Entity %s violates domain rules: %s",
                        entity.getId(), entity.getProblemsSummary());
                return false;
              }
              return true;
            })
            .toList();
  }

  @Override
  public boolean existsByCnpj(Cnpj cnpj) {
    if (cnpj == null) {
      return false;
    }
    return repo.existsByCnpj(cnpj.toString());
  }
}