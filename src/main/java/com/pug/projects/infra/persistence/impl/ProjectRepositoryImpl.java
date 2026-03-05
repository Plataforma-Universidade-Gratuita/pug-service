package com.pug.projects.infra.persistence.impl;

import com.pug.projects.domain.Project;
import com.pug.projects.domain.ProjectRepository;
import com.pug.projects.infra.ProjectMapper;
import com.pug.projects.infra.persistence.ProjectEntity;
import com.pug.shared.utils.StringUtils;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.util.Optional;
import java.util.UUID;

/**
 * Implementation of the {@link ProjectRepository} utilizing Hibernate ORM with Panache.
 */
@ApplicationScoped
public class ProjectRepositoryImpl
        implements ProjectRepository, PanacheRepositoryBase<ProjectEntity, UUID> {

  @Transactional
  @Override
  public Project persist(Project entity) {
    if (entity == null) return null;
    var e = ProjectMapper.toEntity(entity);
    persistAndFlush(e);
    return ProjectMapper.toDomain(e);
  }

  @Transactional
  @Override
  public void update(Project entity) {
    if (entity == null || entity.getId() == null) return;
    ProjectEntity managed = findById(entity.getId());
    if (managed != null) {
      ProjectMapper.copy(entity, managed);
    }
  }

  @Transactional
  @Override
  public boolean deleteById(UUID id) {
    if (id == null) return false;
    var deleted = PanacheRepositoryBase.super.deleteById(id);
    flush();
    return deleted;
  }

  @Override
  public Optional<Project> findOptionalById(UUID id) {
    return findByIdOptional(id).map(ProjectMapper::toDomain);
  }

  @Override
  public boolean existsByNameAndEntityId(String name, UUID entityId) {
    if (StringUtils.isEmpty(name) || entityId == null) return false;
    return count("name = ?1 and entityId = ?2", name, entityId) > 0;
  }
}