package com.pug.academic.infra.persistence;

import com.pug.academic.domain.Course;
import com.pug.academic.domain.CourseRepository;
import com.pug.academic.infra.CourseMapper;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** Repository implementation for Course aggregate. */
@ApplicationScoped
public class CourseRepositoryImpl
    implements CourseRepository, PanacheRepositoryBase<CourseEntity, UUID> {

  @Inject EntityManager entityManager;

  @Transactional
  @Override
  public Course persist(Course course) {
    if (course == null) {
      return null;
    }
    var e = CourseMapper.toEntity(course);
    persistAndFlush(e);
    return CourseMapper.toDomain(e);
  }

  @Transactional
  @Override
  public List<Course> persistAll(Iterable<Course> courses) {
    if (courses == null || !courses.iterator().hasNext()) {
      return List.of();
    }
    var batch = new ArrayList<CourseEntity>();
    for (var d : courses) {
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
    if (ids == null || !ids.iterator().hasNext()) {
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
  public List<Course> listAllByIds(Iterable<UUID> ids) {
    if (ids == null || !ids.iterator().hasNext()) {
      return List.of();
    }
    return find("id in ?1", ids).list().stream().map(CourseMapper::toDomain).toList();
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
