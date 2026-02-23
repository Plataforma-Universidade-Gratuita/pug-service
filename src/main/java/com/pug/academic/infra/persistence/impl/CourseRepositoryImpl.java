package com.pug.academic.infra.persistence.impl;

import com.pug.academic.domain.Course;
import com.pug.academic.domain.CourseRepository;
import com.pug.academic.infra.CourseMapper;
import com.pug.academic.infra.persistence.CourseEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** Repository implementation for Course aggregate. */
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
  public boolean deleteById(UUID id) {
    if (id == null) {
      return false;
    }
    var deleted = PanacheRepositoryBase.super.deleteById(id);
    flush();
    return deleted;
  }

  @Override
  public Optional<Course> findOptionalById(UUID id) {
    Optional<CourseEntity> entityOpt = findByIdOptional(id);
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
}
