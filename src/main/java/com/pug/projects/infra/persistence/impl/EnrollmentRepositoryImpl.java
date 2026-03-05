package com.pug.projects.infra.persistence.impl;

import com.pug.projects.domain.Enrollment;
import com.pug.projects.domain.EnrollmentRepository;
import com.pug.projects.infra.EnrollmentMapper;
import com.pug.projects.infra.persistence.EnrollmentEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.util.Optional;
import java.util.UUID;

/**
 * Implementation of the {@link EnrollmentRepository} utilizing Hibernate ORM with Panache.
 */
@ApplicationScoped
public class EnrollmentRepositoryImpl
        implements EnrollmentRepository,
        PanacheRepositoryBase<EnrollmentEntity, EnrollmentEntity.EnrollmentsId> {

  @Transactional
  @Override
  public Enrollment persist(Enrollment entity) {
    if (entity == null) return null;
    var e = EnrollmentMapper.toEntity(entity);
    persistAndFlush(e);
    return EnrollmentMapper.toDomain(e);
  }

  @Transactional
  @Override
  public void update(Enrollment entity) {
    if (entity == null) return;
    var id = new EnrollmentEntity.EnrollmentsId(
            entity.getIdentifier().getProjectId(), entity.getIdentifier().getStudentId());

    EnrollmentEntity managed = findById(id);
    if (managed != null) {
      EnrollmentMapper.copy(entity, managed);
    }
  }

  @Transactional
  @Override
  public boolean deleteByIds(UUID projectId, UUID studentId) {
    if (projectId == null || studentId == null) return false;
    var id = new EnrollmentEntity.EnrollmentsId(projectId, studentId);
    var deleted = PanacheRepositoryBase.super.deleteById(id);
    flush();
    return deleted;
  }

  @Override
  public Optional<Enrollment> findOptionalByIds(UUID projectId, UUID studentId) {
    if (projectId == null || studentId == null) return Optional.empty();
    var id = new EnrollmentEntity.EnrollmentsId(projectId, studentId);
    return findByIdOptional(id).map(EnrollmentMapper::toDomain);
  }

  @Override
  public boolean existsByIds(UUID projectId, UUID studentId) {
    if (projectId == null || studentId == null) return false;
    var id = new EnrollmentEntity.EnrollmentsId(projectId, studentId);
    return count("id", id) > 0;
  }
}