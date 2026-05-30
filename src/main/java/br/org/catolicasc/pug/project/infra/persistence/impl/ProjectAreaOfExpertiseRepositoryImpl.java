package br.org.catolicasc.pug.project.infra.persistence.impl;

import br.org.catolicasc.pug.project.domain.ProjectAreaOfExpertise;
import br.org.catolicasc.pug.project.domain.ProjectAreaOfExpertiseRepository;
import br.org.catolicasc.pug.project.infra.ProjectAreaOfExpertiseMapper;
import br.org.catolicasc.pug.project.infra.persistence.ProjectAreaOfExpertiseEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/** Implementation of the {@link ProjectAreaOfExpertiseRepository} utilizing Hibernate ORM with Panache. */
@ApplicationScoped
public class ProjectAreaOfExpertiseRepositoryImpl
    implements ProjectAreaOfExpertiseRepository,
        PanacheRepositoryBase<ProjectAreaOfExpertiseEntity, ProjectAreaOfExpertiseEntity.ProjectsAreaOfExpertiseId> {

  /** {@inheritDoc} */
  @Transactional
  @Override
  public boolean delete(ProjectAreaOfExpertise association) {
    if (association == null) {
      return false;
    }

    ProjectAreaOfExpertiseEntity entity = ProjectAreaOfExpertiseMapper.toEntity(association);
    if (entity == null || entity.getId() == null) {
      return false;
    }

    long deleted = delete("id", entity.getId());
    flush();
    return deleted > 0;
  }

  /** {@inheritDoc} */
  @Transactional
  @Override
  public long deleteAllByProjectId(UUID projectId) {
    if (projectId == null) {
      return 0;
    }
    long deleted = delete("id.projectId", projectId);
    flush();
    return deleted;
  }

  /** {@inheritDoc} */
  @Transactional
  @Override
  public long deleteAllByAreaOfExpertiseId(UUID areaOfExpertiseId) {
    if (areaOfExpertiseId == null) {
      return 0;
    }
    long deleted = delete("id.areaOfExpertiseId", areaOfExpertiseId);
    flush();
    return deleted;
  }

  /** {@inheritDoc} */
  @Override
  public Set<UUID> findAllAreaOfExpertiseIdsByProjectId(UUID projectId) {
    if (projectId == null) {
      return Set.of();
    }

    return find("id.projectId", projectId).stream()
        .map(ProjectAreaOfExpertiseEntity::getId)
        .filter(Objects::nonNull)
        .map(ProjectAreaOfExpertiseEntity.ProjectsAreaOfExpertiseId::getAreaOfExpertiseId)
        .collect(Collectors.toSet());
  }

  /** {@inheritDoc} */
  @Override
  public Set<UUID> findAllProjectIdsByAreaOfExpertiseId(UUID areaOfExpertiseId) {
    if (areaOfExpertiseId == null) {
      return Set.of();
    }

    return find("id.areaOfExpertiseId", areaOfExpertiseId).stream()
        .map(ProjectAreaOfExpertiseEntity::getId)
        .filter(Objects::nonNull)
        .map(ProjectAreaOfExpertiseEntity.ProjectsAreaOfExpertiseId::getProjectId)
        .collect(Collectors.toSet());
  }

  /** {@inheritDoc} */
  @Transactional
  @Override
  public ProjectAreaOfExpertise persist(ProjectAreaOfExpertise association) {
    if (association == null) {
      return null;
    }

    ProjectAreaOfExpertiseEntity entity = ProjectAreaOfExpertiseMapper.toEntity(association);
    persistAndFlush(entity);

    return ProjectAreaOfExpertiseMapper.toDomain(entity);
  }
}
