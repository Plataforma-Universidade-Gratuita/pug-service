package com.pug.project.infra.persistence.impl;

import com.pug.project.domain.Project;
import com.pug.project.domain.ProjectRepository;
import com.pug.project.infra.ProjectMapper;
import com.pug.project.infra.persistence.ProjectEntity;
import com.pug.shared.utils.StringUtils;
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
  @Transactional
  @Override
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
  public Optional<Project> findOptionalById(UUID id) {
    return findByIdOptional(id).map(ProjectMapper::toDomain);
  }

  /** {@inheritDoc} */
  @Transactional
  @Override
  public Project persist(Project entity) {
    if (entity == null) {
      return null;
    }
    var e = ProjectMapper.toEntity(entity);
    persistAndFlush(e);
    return ProjectMapper.toDomain(e);
  }

  /** {@inheritDoc} */
  @Transactional
  @Override
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
