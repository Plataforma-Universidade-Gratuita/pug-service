package br.org.catolicasc.pug.academic.infra.persistence.impl;

import br.org.catolicasc.pug.academic.domain.FormerStudent;
import br.org.catolicasc.pug.academic.domain.FormerStudentRepository;
import br.org.catolicasc.pug.academic.infra.FormerStudentMapper;
import br.org.catolicasc.pug.academic.infra.persistence.FormerStudentEntity;
import br.org.catolicasc.pug.shared.utils.CollectionUtils;
import br.org.catolicasc.pug.shared.utils.StringUtils;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementation of the {@link FormerStudentRepository} utilizing Hibernate ORM with Panache.
 *
 * <p>This application-scoped bean bridges the pure domain repository interface with the underlying
 * database infrastructure. It manages transaction boundaries, entity state transitions, and the
 * mapping of complex nested Value Objects into the flattened {@link FormerStudentEntity}.
 */
@ApplicationScoped
public class FormerFormerStudentRepositoryImpl
    implements FormerStudentRepository, PanacheRepositoryBase<FormerStudentEntity, UUID> {

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
  public boolean existsAnyByRegistrations(List<String> registrations) {
    if (CollectionUtils.isEmpty(registrations)) {
      return false;
    }
    return count("academicRegistration in ?1", registrations) > 0;
  }

  /** {@inheritDoc} */
  @Override
  public boolean existsByCourseId(UUID courseId) {
    if (courseId == null) {
      return false;
    }
    return count("courseId", courseId) > 0;
  }

  /** {@inheritDoc} */
  @Override
  public boolean existsByRegistration(String registration) {
    if (StringUtils.isEmpty(registration)) {
      return false;
    }
    return count("academicRegistration", registration) > 0;
  }

  /** {@inheritDoc} */
  @Override
  public Optional<FormerStudent> findOptionalById(UUID id) {
    Optional<FormerStudentEntity> entityOpt = findByIdOptional(id);
    return entityOpt.map(FormerStudentMapper::toDomain);
  }

  /** {@inheritDoc} */
  @Transactional
  @Override
  public FormerStudent persist(FormerStudent formerStudent) {
    if (formerStudent == null) {
      return null;
    }
    FormerStudentEntity e = FormerStudentMapper.toEntity(formerStudent);
    persistAndFlush(e);
    return FormerStudentMapper.toDomain(e);
  }

  /** {@inheritDoc} */
  @Transactional
  @Override
  public List<FormerStudent> persistAll(List<FormerStudent> students) {
    if (CollectionUtils.isEmpty(students)) {
      return List.of();
    }
    List<FormerStudentEntity> entities = students.stream().map(FormerStudentMapper::toEntity).toList();
    persist(entities);
    flush();

    return entities.stream().map(FormerStudentMapper::toDomain).toList();
  }

  /** {@inheritDoc} */
  @Transactional
  @Override
  public void update(FormerStudent formerStudent) {
    if (formerStudent == null || formerStudent.getAccountId() == null) {
      return;
    }
    FormerStudentEntity entity = findById(formerStudent.getAccountId());
    if (entity != null) {
      FormerStudentMapper.copy(formerStudent, entity);
    }
  }
}

