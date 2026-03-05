package com.pug.partner.service.impl;

import com.pug.geo.service.CityService;
import com.pug.partner.domain.Entity;
import com.pug.partner.domain.EntityRepository;
import com.pug.partner.domain.vos.Cnpj;
import com.pug.partner.service.EntityService;
import com.pug.partner.service.StaffService;
import com.pug.partner.service.dtos.EntityCreateCommand;
import com.pug.partner.service.dtos.EntityUpdateCommand;
import com.pug.partner.service.utils.EntityProcessor;
import com.pug.partner.service.utils.ExceptionHelper;
import com.pug.projects.service.ProjectService;
import com.pug.shared.exceptions.AppValidationException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.UUID;
import org.jboss.logging.Logger;

/**
 * Implementation of the {@link EntityService} command interface.
 *
 * <p>This application-scoped service orchestrates state mutations for partner organizations. It
 * manages transaction boundaries, enforces cross-domain constraints (like verifying the city exists
 * via {@link CityService}), and manages the lifecycle cascading to {@link StaffService} during
 * deletion operations.
 */
@ApplicationScoped
public class EntityServiceImpl implements EntityService {

  private static final Logger LOG = Logger.getLogger(EntityServiceImpl.class);

  @Inject EntityRepository repo;

  @Inject CityService cityService;

  @Inject StaffService staffService;

  @Inject ProjectService projectService;

  /** {@inheritDoc} */
  @Transactional
  @Override
  public boolean delete(UUID id) {
    LOG.debugf("Attempting to delete Entity ID: %s", id);
    if (id == null) {
      return false;
    }

    if (projectService.existsAnyByEntityId(id)) {
      LOG.warnf("Delete failed: Entity ID %s has registered projects", id);
      throw ExceptionHelper.entityHasProjects();
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

  /** {@inheritDoc} */
  @Override
  public boolean existsAnyByCityId(UUID cityId) {
    if (cityId == null) {
      return false;
    }
    return repo.existsByCityId(cityId);
  }

  /**
   * Checks if a Partner Entity with the given CNPJ already exists.
   *
   * @param cnpj the validated CNPJ to check for existence
   * @return {@code true} if an entity with the given CNPJ exists, {@code false} otherwise
   */
  private boolean existsByCnpj(Cnpj cnpj) {
    if (cnpj == null) {
      return false;
    }
    return repo.existsByCnpj(cnpj.toString());
  }

  /** {@inheritDoc} */
  @Override
  public Entity getById(UUID id) {
    Entity entity =
        repo.findOptionalById(id)
            .orElseThrow(
                () -> {
                  LOG.debugf("Entity lookup failed: ID %s not found", id);
                  return ExceptionHelper.entityNotFound();
                });

    if (entity.hasFieldErrors()) {
      LOG.errorf(
          "DATA CORRUPTION DETECTED: Entity %s violates domain rules: %s",
          id, entity.getProblemsSummary());
      throw ExceptionHelper.entityNotFound();
    }

    return entity;
  }

  /** {@inheritDoc} */
  @Transactional
  @Override
  public Entity save(EntityCreateCommand cmd) {
    LOG.debugf("Attempting to create Entity: %s (CNPJ: %s)", cmd.name(), cmd.cnpjString());
    if (cmd.cityId() != null) {
      cityService.getById(cmd.cityId());
    }

    Entity entityToPersist =
        EntityProcessor.processCreateInput(
            cmd.cnpjString(), cmd.name(), cmd.cityId(), cmd.address());

    if (entityToPersist.hasFieldErrors()) {
      throw new AppValidationException(entityToPersist.getFieldErrors());
    }

    if (existsByCnpj(entityToPersist.getCnpj())) {
      LOG.warnf("Creation failed: Entity with CNPJ %s already exists", entityToPersist.getCnpj());
      throw ExceptionHelper.entityAlreadyExists();
    }

    Entity savedEntity = repo.persist(entityToPersist);
    LOG.infof("Entity created successfully. ID: %s", savedEntity.getId());
    return savedEntity;
  }

  /** {@inheritDoc} */
  @Transactional
  @Override
  public Entity update(UUID id, EntityUpdateCommand cmd) {
    LOG.debugf("Attempting to update Entity ID: %s", id);
    Entity current = getById(id);
    if (cmd.cityId() != null) {
      cityService.getById(cmd.cityId());
    }

    Entity updatedEntity =
        EntityProcessor.processUpdateInput(current, cmd.name(), cmd.cityId(), cmd.address());

    if (updatedEntity.hasFieldErrors()) {
      throw new AppValidationException(updatedEntity.getFieldErrors());
    }

    repo.update(updatedEntity);
    LOG.infof("Entity updated successfully. ID: %s", id);
    return getById(id);
  }
}
