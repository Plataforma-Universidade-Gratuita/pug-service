package com.pug.academic.infra.queries;

import static java.util.stream.Collectors.toMap;

import com.pug.academic.infra.persistence.CourseEntity;
import com.pug.academic.infra.read.CourseQueries;
import com.pug.academic.infra.read.dtos.CourseView;
import com.pug.academic.infra.read.dtos.SchoolView;
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
import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.session.SearchSession;

/** Implementation of CourseQueries using JPA and Hibernate Search. */
@ApplicationScoped
@Transactional(Transactional.TxType.SUPPORTS)
public class CourseQueriesImpl implements CourseQueries {

  @Inject EntityManager entityManager;

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
  public List<CourseView> listAllByIds(Iterable<UUID> ids) {
    if (ids == null) {
      return List.of();
    }
    List<UUID> list = new ArrayList<>();
    ids.forEach(
        u -> {
          if (u != null) {
            list.add(u);
          }
        });
    if (list.isEmpty()) {
      return List.of();
    }

    var q =
        entityManager.createQuery(
            "select new com.pug.academic.infra.read.dtos.CourseView("
                + "c.id, c.name, "
                + "new com.pug.academic.infra.read.dtos.SchoolView(s.id, s.name)) "
                + "from CourseEntity c join SchoolEntity s "
                + "where c.id in :ids "
                + "order by c.name asc",
            CourseView.class);
    q.setParameter("ids", list);
    return q.getResultList();
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
    if (query == null || query.isBlank()) {
      return List.of();
    }

    String[] tokens = query.split("\\s+");
    SearchSession s = Search.session(entityManager);

    List<CourseEntity> hits =
        s.search(CourseEntity.class)
            .where(
                f ->
                    f.bool(
                        b -> {
                          b.should(
                              f.wildcard().field("name_exact").matching(query + "*").boost(8f));
                          b.should(
                              f.wildcard()
                                  .field("name_exact")
                                  .matching("*" + query + "*")
                                  .boost(6f));
                          for (String t : tokens) {
                            if (t.length() >= 3) {
                              b.should(
                                  f.wildcard()
                                      .field("name_exact")
                                      .matching("*" + t + "*")
                                      .boost(3f));
                            }
                          }
                          b.should(f.match().field("name").matching(query).fuzzy(1).boost(4f));
                          b.should(f.match().field("name_auto").matching(query).boost(2f));
                        }))
            .sort(f -> f.score().then().field("name_sort"))
            .fetchAllHits();

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
