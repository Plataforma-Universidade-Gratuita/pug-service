package br.org.catolicasc.pug.academic.infra.persistence.impl;

import br.org.catolicasc.pug.academic.domain.AreaOfExpertise;
import br.org.catolicasc.pug.academic.domain.FormerStudent;
import br.org.catolicasc.pug.academic.domain.FormerStudentRepository;
import br.org.catolicasc.pug.academic.infra.AreaOfExpertiseMapper;
import br.org.catolicasc.pug.academic.infra.FormerStudentMapper;
import br.org.catolicasc.pug.academic.infra.persistence.AreaOfExpertiseEntity;
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
public class FormerStudentRepositoryImpl
    implements FormerStudentRepository, PanacheRepositoryBase<FormerStudentEntity, UUID> {

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
  public AreaOfExpertise findAreaOfExpertise(UUID id) {
    if (id == null) {
      return null;
    }

    AreaOfExpertiseEntity entity =
        getEntityManager()
            .createQuery(
                """
                select area
                from FormerStudentEntity formerStudent
                join CourseEntity course on course.id = formerStudent.courseId
                join AreaOfExpertiseEntity area on area.id = course.areaOfExpertiseId
                where formerStudent.id = :id
                """,
                AreaOfExpertiseEntity.class)
            .setParameter("id", id)
            .getResultStream()
            .findFirst()
            .orElse(null);

    return AreaOfExpertiseMapper.toDomain(entity);
  }

  /** {@inheritDoc} */
  @Override
  public Optional<FormerStudent> findOptionalById(UUID id) {
    Optional<FormerStudentEntity> entityOpt = findByIdOptional(id);
    return entityOpt.map(FormerStudentMapper::toDomain);
  }

  /** {@inheritDoc} */
  @Override
  @Transactional
  public FormerStudent persist(FormerStudent formerStudent) {
    if (formerStudent == null) {
      return null;
    }
    FormerStudentEntity e = FormerStudentMapper.toEntity(formerStudent);
    persistAndFlush(e);
    return FormerStudentMapper.toDomain(e);
  }

  /** {@inheritDoc} */
  @Override
  @Transactional
  public List<FormerStudent> persistAll(List<FormerStudent> formerStudents) {
    if (CollectionUtils.isEmpty(formerStudents)) {
      return List.of();
    }
    List<FormerStudentEntity> entities =
        formerStudents.stream().map(FormerStudentMapper::toEntity).toList();
    persist(entities);
    flush();

    return entities.stream().map(FormerStudentMapper::toDomain).toList();
  }

  /** {@inheritDoc} */
  @Override
  @Transactional
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
