package com.pug.academic.infra.persistence;

import com.pug.academic.domain.Course;
import com.pug.academic.domain.CourseRepository;
import com.pug.academic.infra.CourseMapper;
import com.pug.shared.utils.CollectionUtils;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository implementation for Course aggregate.
 */
@ApplicationScoped
public class CourseRepositoryImpl
        implements CourseRepository, PanacheRepositoryBase<CourseEntity, UUID> {

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

  @Transactional
  @Override
  public List<Course> persistAll(Iterable<Course> entities) {
    if (CollectionUtils.isEmpty(entities)) {
      return List.of();
    }
    var batch = new ArrayList<CourseEntity>();
    for (var d : entities) {
      if (d != null) {
        batch.add(CourseMapper.toEntity(d));
      }
    }
    if (batch.isEmpty()) {
      return List.of();
    }
    persist(batch);
    flush();
    return batch.stream().map(CourseMapper::toDomain).toList();
  }

  @Transactional
  @Override
  public long deleteByIds(Iterable<UUID> ids) {
    if (CollectionUtils.isEmpty(ids)) {
      return 0L;
    }
    long n = delete("id in ?1", ids);
    flush();
    getEntityManager().clear();
    return n;
  }

  @Override
  public Optional<Course> findOptionalById(UUID id) {
    return findByIdOptional(id).map(CourseMapper::toDomain);
  }

  @Override
  public Optional<Course> findOptionalByName(String name) {
    return find("name", name).firstResultOptional().map(CourseMapper::toDomain);
  }

  @Override
  public List<Course> listAllCourses() {
    return listAll().stream().map(CourseMapper::toDomain).toList();
  }

  @Override
  public List<Course> listAllBySchoolId(UUID schoolId) {
    return find("schoolId", schoolId).list().stream().map(CourseMapper::toDomain).toList();
  }

  @Override
  public boolean existsByName(String name) {
    return find("name", name).firstResultOptional().isPresent();
  }

  @Override
  public boolean existsAnyByNameIn(Iterable<String> names) {
    if (CollectionUtils.isEmpty(names)) {
      return false;
    }
    return find("name in ?1", names).firstResultOptional().isPresent();
  }

  @Override
  public void update(Course course) {
    if (course == null || course.getId() == null) {
      return;
    }
    CourseEntity managed = findById(course.getId());
    if (managed == null) {
      return;
    }
    CourseMapper.copy(course, managed);
  }
}
