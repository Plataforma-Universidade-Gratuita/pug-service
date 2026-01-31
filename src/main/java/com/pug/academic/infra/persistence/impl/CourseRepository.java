package com.pug.academic.infra.persistence.impl;

import com.pug.academic.domain.Course;
import com.pug.academic.domain.ICourseRepository;
import com.pug.academic.infra.CourseMapper;
import com.pug.academic.infra.persistence.CourseEntity;
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
public class CourseRepository
        implements ICourseRepository, PanacheRepositoryBase<CourseEntity, UUID> {

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

    var domainObjects = new ArrayList<Course>();
    for (CourseEntity e : batch) {
      domainObjects.add(CourseMapper.toDomain(e));
    }
    return domainObjects;
  }

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
    Optional<CourseEntity> entityOpt = findByIdOptional(id);
    return entityOpt.map(CourseMapper::toDomain);
  }

  @Override
  public Optional<Course> findOptionalByName(String name) {
    Optional<CourseEntity> entityOpt = find("name", name).firstResultOptional();
    return entityOpt.map(CourseMapper::toDomain);
  }

  @Override
  public List<Course> listAllCourses() {
    var domainList = new ArrayList<Course>();
    for (CourseEntity entity : listAll()) {
      domainList.add(CourseMapper.toDomain(entity));
    }
    return domainList;
  }

  @Override
  public List<Course> listAllBySchoolId(UUID schoolId) {
    var domainList = new ArrayList<Course>();
    for (CourseEntity entity : find("schoolId", schoolId).list()) {
      domainList.add(CourseMapper.toDomain(entity));
    }
    return domainList;
  }

  @Override
  public boolean existsByName(String name) {
    return count("name = ?1", name) > 0;
  }

  @Override
  public boolean existsAnyByNameIn(Iterable<String> names) {
    if (CollectionUtils.isEmpty(names)) {
      return false;
    }
    return count("name in ?1", names) > 0;
  }
}