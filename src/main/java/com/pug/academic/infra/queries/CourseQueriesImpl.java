package com.pug.academic.infra.queries;

import com.pug.academic.infra.persistence.CourseEntity;
import com.pug.academic.infra.read.CourseQueries;
import com.pug.academic.infra.read.dtos.CourseView;
import com.pug.academic.infra.read.dtos.SchoolView;
import com.pug.shared.infra.search.HibernateSearchUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static java.util.stream.Collectors.toMap;

/**
 * Implementation of CourseQueries using JPA and Hibernate Search.
 */
@ApplicationScoped
@Transactional(Transactional.TxType.SUPPORTS)
public class CourseQueriesImpl implements CourseQueries {

  @Inject
  EntityManager entityManager;

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
                            + "from CourseEntity c join SchoolEntity s "
                            + "where c.id = :id",
                    CourseView.class);
    q.setParameter("id", id);
    return q.getResultStream().findFirst();
  }

  @Override
  public List<CourseView> listAllCourses() {
    var q =
            entityManager.createQuery(
                    "select new com.pug.academic.infra.read.dtos.CourseView("
                            + "c.id, c.name, "
                            + "new com.pug.academic.infra.read.dtos.SchoolView(s.id, s.name)) "
                            + "from CourseEntity c join SchoolEntity s "
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
                            + "from CourseEntity c join SchoolEntity s "
                            + "where s.id = :sid "
                            + "order by c.name asc",
                    CourseView.class);
    q.setParameter("sid", schoolId);
    return q.getResultList();
  }

  @Override
  public List<CourseView> searchByName(String query) {
    List<CourseEntity> hits = HibernateSearchUtils.searchByName(entityManager, CourseEntity.class, query);

    if (hits.isEmpty()) {
      return List.of();
    }

    Set<UUID> schoolIds = new HashSet<>(hits.size());
    for (CourseEntity c : hits) {
      if (c.getSchoolId() != null) {
        schoolIds.add(c.getSchoolId());
      }
    }

    Map<UUID, SchoolView> schoolsById =
            schoolIds.isEmpty()
                    ? Map.of()
                    : entityManager
                    .createQuery(
                            "select new com.pug.academic.infra.read.dtos.SchoolView(s.id, s.name) "
                                    + "from SchoolEntity s where s.id in :ids",
                            SchoolView.class)
                    .setParameter("ids", schoolIds)
                    .getResultList()
                    .stream()
                    .collect(toMap(SchoolView::id, sv -> sv));

    List<CourseView> out = new ArrayList<>(hits.size());
    for (CourseEntity c : hits) {
      out.add(new CourseView(c.getId(), c.getName(), schoolsById.get(c.getSchoolId())));
    }
    return out;
  }
}
