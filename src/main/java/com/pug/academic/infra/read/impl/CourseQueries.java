package com.pug.academic.infra.read.impl;

import static java.util.stream.Collectors.toMap;

import com.pug.academic.infra.persistence.CourseEntity;
import com.pug.academic.infra.read.ICourseQueries;
import com.pug.academic.infra.read.dtos.CourseView;
import com.pug.academic.infra.read.dtos.SchoolView;
import com.pug.shared.infra.search.HibernateSearchUtils;
import com.pug.shared.utils.StringUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/** Implementation of CourseQueries using JPA and Hibernate Search. */
@ApplicationScoped
@Transactional(Transactional.TxType.SUPPORTS)
public class CourseQueries implements ICourseQueries {

  @Inject EntityManager entityManager;

  /**
   * Helper to convert CourseEntity and its associated SchoolView to CourseView.
   *
   * @param courseEntity The CourseEntity.
   * @param schoolView The associated SchoolView.
   * @return The CourseView.
   */
  private CourseView toView(CourseEntity courseEntity, SchoolView schoolView) {
    if (courseEntity == null) {
      return null;
    }
    return new CourseView(courseEntity.getId(), courseEntity.getName(), schoolView);
  }

  @Override
  public Optional<CourseView> findOptionalById(UUID id) {
    if (id == null) {
      return Optional.empty();
    }
    var q =
        entityManager.createQuery(
            "select new com.pug.academic.infra.read.dtos.CourseView("
                + "c.id, c.name, "
                + "new com.pug.academic.infra.read.dtos.SchoolView(s.id, s.name)) "
                + "from CourseEntity c join SchoolEntity s ON c.schoolId = s.id "
                + "where c.id = :id",
            CourseView.class);
    q.setParameter("id", id);
    return q.getResultStream().findFirst();
  }

  @Override
  public Optional<CourseView> findOptionalByName(String name) {
    if (StringUtils.isEmpty(name)) {
      return Optional.empty();
    }
    var q =
        entityManager.createQuery(
            "select new com.pug.academic.infra.read.dtos.CourseView("
                + "c.id, c.name, "
                + "new com.pug.academic.infra.read.dtos.SchoolView(s.id, s.name)) "
                + "from CourseEntity c join SchoolEntity s ON c.schoolId = s.id "
                + "where c.name = :name",
            CourseView.class);
    q.setParameter("name", name);
    return q.getResultStream().findFirst();
  }

  @Override
  public List<CourseView> listAllCourses() {
    var q =
        entityManager.createQuery(
            "select new com.pug.academic.infra.read.dtos.CourseView("
                + "c.id, c.name, "
                + "new com.pug.academic.infra.read.dtos.SchoolView(s.id, s.name)) "
                + "from CourseEntity c join SchoolEntity s ON c.schoolId = s.id "
                + "order by c.name asc",
            CourseView.class);
    return q.getResultList();
  }

  @Override
  public List<CourseView> listAllBySchoolId(UUID schoolId) {
    if (schoolId == null) {
      return List.of();
    }
    var q =
        entityManager.createQuery(
            "select new com.pug.academic.infra.read.dtos.CourseView("
                + "c.id, c.name, "
                + "new com.pug.academic.infra.read.dtos.SchoolView(s.id, s.name)) "
                + "from CourseEntity c join SchoolEntity s ON c.schoolId = s.id "
                + "where s.id = :sid "
                + "order by c.name asc",
            CourseView.class);
    q.setParameter("sid", schoolId);
    return q.getResultList();
  }

  @Override
  public List<CourseView> searchByName(String query) {
    List<CourseEntity> hits =
        HibernateSearchUtils.searchByName(entityManager, CourseEntity.class, query);

    if (hits.isEmpty()) {
      return List.of();
    }

    Set<UUID> schoolIds = new HashSet<>();
    for (CourseEntity c : hits) {
      if (c.getSchoolId() != null) {
        schoolIds.add(c.getSchoolId());
      }
    }

    Map<UUID, SchoolView> schoolsById = new HashMap<>();
    if (!schoolIds.isEmpty()) {
      schoolsById =
          entityManager
              .createQuery(
                  "select new com.pug.academic.infra.read.dtos.SchoolView(s.id, s.name) "
                      + "from SchoolEntity s where s.id in :ids",
                  SchoolView.class)
              .setParameter("ids", schoolIds)
              .getResultList()
              .stream()
              .collect(toMap(SchoolView::id, sv -> sv));
    }

    List<CourseView> out = new ArrayList<>(hits.size());
    for (CourseEntity c : hits) {
      out.add(toView(c, schoolsById.get(c.getSchoolId())));
    }
    return out;
  }
}
