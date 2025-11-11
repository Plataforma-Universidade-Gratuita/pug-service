package com.pug.academic.infra.queries;

import com.pug.academic.infra.persistence.SchoolEntity;
import com.pug.academic.infra.read.SchoolQueries;
import com.pug.academic.infra.read.dtos.SchoolView;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.session.SearchSession;

/** Implementation of SchoolQueries using JPA and Hibernate Search. */
@ApplicationScoped
@Transactional(Transactional.TxType.SUPPORTS)
public class SchoolQueriesImpl implements SchoolQueries {

  @Inject EntityManager entityManager;

  private static SchoolView toView(SchoolEntity s) {
    if (s == null) {
      return null;
    }
    return new SchoolView(s.getId(), s.getName());
  }

  @Override
  public Optional<SchoolView> findOptionalById(UUID id) {
    if (id == null) {
      return Optional.empty();
    }
    var q =
        entityManager.createQuery(
            "select new com.pug.academic.infra.read.dtos.SchoolView("
                + "s.id, s.name) "
                + "from SchoolEntity s where s.id = :id",
            SchoolView.class);
    q.setParameter("id", id);
    return q.getResultStream().findFirst();
  }

  @Override
  public List<SchoolView> listAllByIds(Iterable<UUID> ids) {
    if (ids == null || !ids.iterator().hasNext()) {
      return List.of();
    }
    var q =
        entityManager.createQuery(
            "select new com.pug.academic.infra.read.dtos.SchoolView("
                + "s.id, s.name) "
                + "from SchoolEntity s where s.id in :ids "
                + "order by s.name asc",
            SchoolView.class);
    q.setParameter("ids", ids);
    return q.getResultList();
  }

  @Override
  public List<SchoolView> listAllSchools() {
    var q =
        entityManager.createQuery(
            "select new com.pug.academic.infra.read.dtos.SchoolView("
                + "s.id, s.name) "
                + "from SchoolEntity s order by s.name asc",
            SchoolView.class);
    return q.getResultList();
  }

  @Override
  public List<SchoolView> searchByName(String key) {
    if (key == null || key.isBlank()) {
      return List.of();
    }

    String[] tokens = key.split("\\s+");
    SearchSession s = Search.session(entityManager);

    List<SchoolEntity> hits =
        s.search(SchoolEntity.class)
            .where(
                f ->
                    f.bool(
                        b -> {
                          b.should(f.wildcard().field("name_exact").matching(key + "*").boost(8f));
                          b.should(
                              f.wildcard().field("name_exact").matching("*" + key + "*").boost(6f));
                          for (String t : tokens) {
                            if (t.length() >= 3) {
                              b.should(
                                  f.wildcard()
                                      .field("name_exact")
                                      .matching("*" + t + "*")
                                      .boost(3f));
                            }
                          }
                          b.should(f.match().field("name").matching(key).fuzzy(1).boost(4f));
                          b.should(f.match().field("name_auto").matching(key).boost(2f));
                        }))
            .sort(f -> f.score().then().field("name_sort"))
            .fetchAllHits();

    if (hits.isEmpty()) {
      return List.of();
    }

    List<SchoolView> out = new ArrayList<>(hits.size());
    for (SchoolEntity e : hits) {
      out.add(toView(e));
    }
    return out;
  }
}
