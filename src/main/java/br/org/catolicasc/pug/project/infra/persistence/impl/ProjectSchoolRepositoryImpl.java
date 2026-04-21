package br.org.catolicasc.pug.project.infra.persistence.impl;

import br.org.catolicasc.pug.project.domain.ProjectSchool;
import br.org.catolicasc.pug.project.domain.ProjectSchoolRepository;
import br.org.catolicasc.pug.project.infra.ProjectSchoolMapper;
import br.org.catolicasc.pug.project.infra.persistence.ProjectSchoolEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/** Implementation of the {@link ProjectSchoolRepository} utilizing Hibernate ORM with Panache. */
@ApplicationScoped
public class ProjectSchoolRepositoryImpl
    implements ProjectSchoolRepository,
        PanacheRepositoryBase<ProjectSchoolEntity, ProjectSchoolEntity.ProjectsBySchoolsId> {

  /** {@inheritDoc} */
  @Transactional
  @Override
  public boolean delete(ProjectSchool association) {
    if (association == null) {
      return false;
    }

    ProjectSchoolEntity entity = ProjectSchoolMapper.toEntity(association);
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
        .map(ProjectSchoolEntity::getId)
        .filter(Objects::nonNull)
        .map(ProjectSchoolEntity.ProjectsBySchoolsId::getSchoolId)
        .collect(Collectors.toSet());
  }

  /** {@inheritDoc} */
  @Override
  public Set<UUID> findAllProjectIdsBySchoolId(UUID schoolId) {
    if (schoolId == null) {
      return Set.of();
    }

    return find("id.schoolId", schoolId).stream()
        .map(ProjectSchoolEntity::getId)
        .filter(Objects::nonNull)
        .map(ProjectSchoolEntity.ProjectsBySchoolsId::getProjectId)
        .collect(Collectors.toSet());
  }

  /** {@inheritDoc} */
  @Transactional
  @Override
  public ProjectSchool persist(ProjectSchool association) {
    if (association == null) {
      return null;
    }

    ProjectSchoolEntity entity = ProjectSchoolMapper.toEntity(association);
    persistAndFlush(entity);

    return ProjectSchoolMapper.toDomain(entity);
  }
}
