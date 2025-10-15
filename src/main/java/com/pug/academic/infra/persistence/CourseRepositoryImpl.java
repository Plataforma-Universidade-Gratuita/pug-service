package com.pug.academic.infra.persistence;

import com.pug.academic.domain.Course;
import com.pug.academic.domain.CourseRepository;
import com.pug.academic.infra.CourseMapper;
import com.pug.shared.infra.persistence.Page;
import com.pug.shared.infra.persistence.PageRequest;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class CourseRepositoryImpl
    implements CourseRepository, PanacheRepositoryBase<CourseEntity, UUID> {

  @PersistenceContext EntityManager em;

  @Override
  public Course save(Course course) {
    var schoolRef = em.getReference(SchoolEntity.class, course.getSchoolId());
    if (course.getId() == null) {
      var e = CourseMapper.toEntity(course, schoolRef);
      persist(e);
      return CourseMapper.toDomain(e);
    } else {
      var e = findById(course.getId());
      CourseMapper.copy(course, e, schoolRef);
      return CourseMapper.toDomain(e);
    }
  }

  @Override
  public Optional<Course> findOptionalById(UUID id) {
    return Optional.ofNullable(findById(id)).map(CourseMapper::toDomain);
  }

  @Override
  public Optional<Course> findByNameIgnoreCase(String name) {
    if (name == null) return Optional.empty();
    return find("lower(name) = ?1", name.toLowerCase(Locale.ROOT))
        .firstResultOptional()
        .map(CourseMapper::toDomain);
  }

  @Override
  public Page<Course> listBySchool(UUID schoolId, PageRequest pr) {
    long total = count("school.id", schoolId);
    List<Course> items =
        find("school.id = ?1 order by name asc", schoolId)
            .page(pr.page(), pr.size())
            .list()
            .stream()
            .map(CourseMapper::toDomain)
            .toList();
    return new Page<>(items, total, pr.page(), pr.size());
  }

  @Override
  public List<Course> listAllBySchool(UUID schoolId) {
    return find("school.id = ?1 order by name asc", schoolId).list().stream()
        .map(CourseMapper::toDomain)
        .toList();
  }
}
