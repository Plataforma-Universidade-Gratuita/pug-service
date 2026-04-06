package com.pug.project.infra.persistence.impl;

import com.pug.project.domain.Enrollment;
import com.pug.project.domain.EnrollmentRepository;
import com.pug.project.domain.vos.EnrollmentIdentifier;
import com.pug.project.infra.EnrollmentMapper;
import com.pug.project.infra.persistence.EnrollmentEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.util.Optional;
import java.util.UUID;

/** Implementation of the {@link EnrollmentRepository} utilizing Hibernate ORM with Panache. */
@ApplicationScoped
public class EnrollmentRepositoryImpl
    implements EnrollmentRepository,
        PanacheRepositoryBase<EnrollmentEntity, EnrollmentEntity.EnrollmentsId> {

  /** {@inheritDoc} */
  @Transactional
  @Override
  public boolean deleteById(EnrollmentIdentifier identifier) {
    if (identifier == null
        || identifier.getProjectId() == null
        || identifier.getStudentId() == null) {
      return false;
    }

    var id =
        new EnrollmentEntity.EnrollmentsId(identifier.getProjectId(), identifier.getStudentId());

    boolean deleted = delete("id", id) > 0;
    flush();
    return deleted;
  }

  /** {@inheritDoc} */
  @Override
  public boolean existsById(EnrollmentIdentifier identifier) {
    if (identifier == null
        || identifier.getProjectId() == null
        || identifier.getStudentId() == null) {
      return false;
    }

    var id =
        new EnrollmentEntity.EnrollmentsId(identifier.getProjectId(), identifier.getStudentId());

    return count("id", id) > 0;
  }

  /** {@inheritDoc} */
  @Override
  public boolean existsByProjectId(UUID projectId) {
    if (projectId == null) {
      return false;
    }
    return count("id.projectId", projectId) > 0;
  }

  /** {@inheritDoc} */
  @Override
  public boolean existsByStudentId(UUID studentId) {
    if (studentId == null) {
      return false;
    }
    return count("id.studentId", studentId) > 0;
  }

  /** {@inheritDoc} */
  @Override
  public Optional<Enrollment> findOptionalById(EnrollmentIdentifier identifier) {
    if (identifier == null
        || identifier.getProjectId() == null
        || identifier.getStudentId() == null) {
      return Optional.empty();
    }

    var id =
        new EnrollmentEntity.EnrollmentsId(identifier.getProjectId(), identifier.getStudentId());

    return findByIdOptional(id).map(EnrollmentMapper::toDomain);
  }

  /** {@inheritDoc} */
  @Transactional
  @Override
  public Enrollment persist(Enrollment entity) {
    if (entity == null) {
      return null;
    }
    EnrollmentEntity e = EnrollmentMapper.toEntity(entity);
    persistAndFlush(e);
    return EnrollmentMapper.toDomain(e);
  }

  /** {@inheritDoc} */
  @Transactional
  @Override
  public void update(Enrollment entity) {
    if (entity == null || entity.getIdentifier() == null) {
      return;
    }

    var id =
        new EnrollmentEntity.EnrollmentsId(
            entity.getIdentifier().getProjectId(), entity.getIdentifier().getStudentId());

    EnrollmentEntity managed = findById(id);
    if (managed != null) {
      EnrollmentMapper.copy(entity, managed);
    }
  }
}
