/*
 * Copyright (c) 2026 Mateus Fernandes and Plataforma Universidade Gratuita.
 * All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized use,
 * copying, modification, distribution, or deployment is prohibited.
 */

package br.org.catolicasc.pug.project.infra.persistence.impl;

import br.org.catolicasc.pug.project.domain.Enrollment;
import br.org.catolicasc.pug.project.domain.EnrollmentRepository;
import br.org.catolicasc.pug.project.domain.vos.EnrollmentIdentifier;
import br.org.catolicasc.pug.project.infra.EnrollmentMapper;
import br.org.catolicasc.pug.project.infra.persistence.EnrollmentEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** Implementation of the {@link EnrollmentRepository} utilizing Hibernate ORM with Panache. */
@ApplicationScoped
public class EnrollmentRepositoryImpl
    implements EnrollmentRepository,
        PanacheRepositoryBase<EnrollmentEntity, EnrollmentEntity.EnrollmentsId> {

  /** {@inheritDoc} */
  @Override
  @Transactional
  public boolean deleteById(EnrollmentIdentifier identifier) {
    if (identifier == null
        || identifier.getProjectId() == null
        || identifier.getFormerStudentId() == null) {
      return false;
    }

    var id =
        new EnrollmentEntity.EnrollmentsId(
            identifier.getProjectId(), identifier.getFormerStudentId());

    boolean deleted = delete("id", id) > 0;
    flush();
    return deleted;
  }

  /** {@inheritDoc} */
  @Override
  public boolean existsById(EnrollmentIdentifier identifier) {
    if (identifier == null
        || identifier.getProjectId() == null
        || identifier.getFormerStudentId() == null) {
      return false;
    }

    return count(
            "id.projectId = ?1 and id.formerStudentId = ?2",
            identifier.getProjectId(),
            identifier.getFormerStudentId())
        > 0;
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
  public boolean existsByFormerStudentId(UUID formerStudentId) {
    if (formerStudentId == null) {
      return false;
    }
    return count("id.formerStudentId", formerStudentId) > 0;
  }

  /** {@inheritDoc} */
  @Override
  public Optional<Enrollment> findOptionalById(EnrollmentIdentifier identifier) {
    if (identifier == null
        || identifier.getProjectId() == null
        || identifier.getFormerStudentId() == null) {
      return Optional.empty();
    }

    var id =
        new EnrollmentEntity.EnrollmentsId(
            identifier.getProjectId(), identifier.getFormerStudentId());

    return findByIdOptional(id).map(EnrollmentMapper::toDomain);
  }

  /** {@inheritDoc} */
  @Override
  public List<Enrollment> listAllByProjectId(UUID projectId) {
    if (projectId == null) {
      return List.of();
    }
    return find("id.projectId", projectId).list().stream().map(EnrollmentMapper::toDomain).toList();
  }

  /** {@inheritDoc} */
  @Override
  public List<Enrollment> listAllByFormerStudentId(UUID formerStudentId) {
    if (formerStudentId == null) {
      return List.of();
    }
    return find("id.formerStudentId", formerStudentId).list().stream()
        .map(EnrollmentMapper::toDomain)
        .toList();
  }

  /** {@inheritDoc} */
  @Override
  @Transactional
  public Enrollment persist(Enrollment entity) {
    if (entity == null) {
      return null;
    }
    EnrollmentEntity e = EnrollmentMapper.toEntity(entity);
    persistAndFlush(e);
    return EnrollmentMapper.toDomain(e);
  }

  /** {@inheritDoc} */
  @Override
  @Transactional
  public void update(Enrollment entity) {
    if (entity == null || entity.getIdentifier() == null) {
      return;
    }

    var id =
        new EnrollmentEntity.EnrollmentsId(
            entity.getIdentifier().getProjectId(), entity.getIdentifier().getFormerStudentId());

    EnrollmentEntity managed = findById(id);
    if (managed != null) {
      EnrollmentMapper.copy(entity, managed);
    }
  }
}
