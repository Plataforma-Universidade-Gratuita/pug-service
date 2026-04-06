package com.pug.project.infra.persistence.impl;

import com.pug.project.domain.ProjectBySchool;
import com.pug.project.domain.ProjectBySchoolRepository;
import com.pug.project.infra.ProjectBySchoolMapper;
import com.pug.project.infra.persistence.ProjectsBySchoolsEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/** Implementation of the {@link ProjectBySchoolRepository} utilizing Hibernate ORM with Panache. */
@ApplicationScoped
public class ProjectBySchoolRepositoryImpl
    implements ProjectBySchoolRepository,
        PanacheRepositoryBase<
            ProjectsBySchoolsEntity, ProjectsBySchoolsEntity.ProjectsBySchoolsId> {

  /** {@inheritDoc} */
  @Transactional
  @Override
  public boolean delete(ProjectBySchool association) {
    if (association == null) {
      return false;
    }

    ProjectsBySchoolsEntity entity = ProjectBySchoolMapper.toEntity(association);
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
  public long deleteAllBySchoolId(UUID schoolId) {
    if (schoolId == null) {
      return 0;
    }
    long deleted = delete("id.schoolId", schoolId);
    flush();
    return deleted;
  }

  /** {@inheritDoc} */
  @Override
  public Set<UUID> findAllSchoolIdsByProjectId(UUID projectId) {
    if (projectId == null) {
      return Set.of();
    }

    return find("id.projectId", projectId).stream()
        .map(ProjectsBySchoolsEntity::getId)
        .filter(Objects::nonNull)
        .map(ProjectsBySchoolsEntity.ProjectsBySchoolsId::getSchoolId)
        .collect(Collectors.toSet());
  }

  /** {@inheritDoc} */
  @Override
  public Set<UUID> findAllProjectIdsBySchoolId(UUID schoolId) {
    if (schoolId == null) {
      return Set.of();
    }

    return find("id.schoolId", schoolId).stream()
        .map(ProjectsBySchoolsEntity::getId)
        .filter(Objects::nonNull)
        .map(ProjectsBySchoolsEntity.ProjectsBySchoolsId::getProjectId)
        .collect(Collectors.toSet());
  }

  /** {@inheritDoc} */
  @Transactional
  @Override
  public ProjectBySchool persist(ProjectBySchool association) {
    if (association == null) {
      return null;
    }

    ProjectsBySchoolsEntity entity = ProjectBySchoolMapper.toEntity(association);
    persistAndFlush(entity);

    return ProjectBySchoolMapper.toDomain(entity);
  }
}
