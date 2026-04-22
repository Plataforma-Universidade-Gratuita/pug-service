package br.org.catolicasc.pug.partner.service.impl;

import br.org.catolicasc.pug.geo.service.CityReadService;
import br.org.catolicasc.pug.partner.domain.Entity;
import br.org.catolicasc.pug.partner.domain.EntityRepository;
import br.org.catolicasc.pug.partner.domain.vos.Cnpj;
import br.org.catolicasc.pug.partner.service.EntityService;
import br.org.catolicasc.pug.partner.service.StaffService;
import br.org.catolicasc.pug.partner.service.dtos.EntityCreateCommand;
import br.org.catolicasc.pug.partner.service.dtos.EntityUpdateCommand;
import br.org.catolicasc.pug.partner.service.utils.EntityProcessor;
import br.org.catolicasc.pug.partner.service.utils.ExceptionHelper;
import br.org.catolicasc.pug.project.service.ProjectService;
import br.org.catolicasc.pug.shared.exceptions.AppValidationException;
import br.org.catolicasc.pug.shared.infra.audit.AuditPublisher;
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
 * via {@link CityReadService}), and manages the lifecycle cascading to {@link StaffService} during
 * deletion operations.
 */
@ApplicationScoped
public class EntityServiceImpl implements EntityService {

  private static final Logger LOG = Logger.getLogger(EntityServiceImpl.class);

  @Inject AuditPublisher auditPublisher;

  @Inject EntityRepository repo;

  @Inject CityReadService cityReadService;

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
      auditPublisher.fireDelete(Entity.class.getName(), id);
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
    return repo.existsByCnpj(cnpj.getValue());
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
      cityReadService.getViewById(cmd.cityId());
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

    auditPublisher.fireCreate(Entity.class.getName(), savedEntity.getId());
    return savedEntity;
  }

  /** {@inheritDoc} */
  @Transactional
  @Override
  public Entity update(UUID id, EntityUpdateCommand cmd) {
    LOG.debugf("Attempting to update Entity ID: %s", id);
    Entity current = getById(id);
    if (cmd.cityId() != null) {
      cityReadService.getViewById(cmd.cityId());
    }

    Entity updatedEntity =
        EntityProcessor.processUpdateInput(current, cmd.name(), cmd.cityId(), cmd.address());

    if (updatedEntity.hasFieldErrors()) {
      throw new AppValidationException(updatedEntity.getFieldErrors());
    }

    repo.update(updatedEntity);
    LOG.infof("Entity updated successfully. ID: %s", id);

    auditPublisher.fireUpdate(Entity.class.getName(), id, current, updatedEntity);
    return getById(id);
  }
}
