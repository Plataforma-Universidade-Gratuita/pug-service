/*
 * Copyright (c) 2026 Mateus Fernandes and Plataforma Universidade Gratuita.
 * All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized use,
 * copying, modification, distribution, or deployment is prohibited.
 */

package br.org.catolicasc.pug.project.infra.persistence.impl;

import br.org.catolicasc.pug.project.domain.Project;
import br.org.catolicasc.pug.project.domain.ProjectRepository;
import br.org.catolicasc.pug.project.domain.enums.ProjectStatus;
import br.org.catolicasc.pug.project.infra.ProjectMapper;
import br.org.catolicasc.pug.project.infra.persistence.ProjectEntity;
import br.org.catolicasc.pug.shared.utils.StringUtils;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import java.util.Optional;
import java.util.UUID;

/** Implementation of the {@link ProjectRepository} utilizing Hibernate ORM with Panache. */
@ApplicationScoped
public class ProjectRepositoryImpl
    implements ProjectRepository, PanacheRepositoryBase<ProjectEntity, UUID> {

  @Inject EntityManager em;

  /** {@inheritDoc} */
  @Override
  @Transactional
  public boolean deleteById(UUID id) {
    if (id == null) {
      return false;
    }
    var deleted = delete("id", id) > 0;
    flush();
    return deleted;
  }

  /** {@inheritDoc} */
  @Override
  public boolean existsByCreatedBy(UUID accountId) {
    if (accountId == null) {
      return false;
    }
    return count("createdBy", accountId) > 0;
  }

  /** {@inheritDoc} */
  @Override
  public boolean existsByEntityId(UUID entityId) {
    if (entityId == null) {
      return false;
    }
    return count("entityId", entityId) > 0;
  }

  /** {@inheritDoc} */
  @Override
  public boolean existsByNameAndEntityId(String name, UUID entityId) {
    if (StringUtils.isEmpty(name) || entityId == null) {
      return false;
    }
    return count("name = ?1 and entityId = ?2", name, entityId) > 0;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isInProgress(UUID id) {
    if (id == null) {
      return false;
    }
    return count("id = ?1 and status = ?2", id, ProjectStatus.IN_PROGRESS.name()) > 0;
  }

  /** {@inheritDoc} */
  @Override
  public Optional<Project> findOptionalById(UUID id) {
    return findByIdOptional(id).map(ProjectMapper::toDomain);
  }

  /** {@inheritDoc} */
  @Override
  @Transactional
  public Project persist(Project entity) {
    if (entity == null) {
      return null;
    }
    var e = ProjectMapper.toEntity(entity);
    persistAndFlush(e);
    return ProjectMapper.toDomain(e);
  }

  /** {@inheritDoc} */
  @Override
  @Transactional
  public void update(Project entity) {
    if (entity == null || entity.getId() == null) {
      return;
    }
    ProjectEntity managed = findById(entity.getId());
    if (managed != null) {
      ProjectMapper.copy(entity, managed);
    }
  }
}
