package com.pug.academic.infra.persistence.impl;

import com.pug.academic.domain.Course;
import com.pug.academic.domain.CourseRepository;
import com.pug.academic.infra.CourseMapper;
import com.pug.academic.infra.persistence.CourseEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementation of the {@link CourseRepository} utilizing Hibernate ORM with Panache.
 *
 * <p>This application-scoped bean bridges the pure domain repository interface with the underlying
 * database infrastructure. It manages transaction boundaries, entity state transitions, and the
 * mapping between domain aggregates and JPA persistence entities.
 */
@ApplicationScoped
public class CourseRepositoryImpl
    implements CourseRepository, PanacheRepositoryBase<CourseEntity, UUID> {

  /** {@inheritDoc} */
  @Transactional
  @Override
  public Course persist(Course entity) {
    if (entity == null) {
      return null;
    }
    var e = CourseMapper.toEntity(entity);
    persistAndFlush(e);
    return CourseMapper.toDomain(e);
  }

  /** {@inheritDoc} */
  @Transactional
  @Override
  public void update(Course course) {
    if (course == null || course.getId() == null) {
      return;
    }
    CourseEntity managed = findById(course.getId());
    if (managed != null) {
      CourseMapper.copy(course, managed);
    }
  }

  /** {@inheritDoc} */
  @Transactional
  @Override
  public boolean deleteById(UUID id) {
    if (id == null) {
      return false;
    }
    var deleted = PanacheRepositoryBase.super.deleteById(id);
    flush();
    return deleted;
  }

  /** {@inheritDoc} */
  @Override
  public Optional<Course> findOptionalById(UUID id) {
    Optional<CourseEntity> entityOpt = findByIdOptional(id);
    return entityOpt.map(CourseMapper::toDomain);
  }

  /** {@inheritDoc} */
  @Override
  public boolean existsByName(String name) {
    return count("name = ?1", name) > 0;
  }
}
