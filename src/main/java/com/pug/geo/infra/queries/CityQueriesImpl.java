package com.pug.geo.infra.queries;

import com.pug.geo.infra.persistence.CityEntity;
import com.pug.geo.infra.read.CityQueries;
import com.pug.geo.infra.read.dtos.CityView;
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

/** Implementation of CityQueries using JPA and Hibernate Search. */
@ApplicationScoped
@Transactional(Transactional.TxType.SUPPORTS)
public class CityQueriesImpl implements CityQueries {

  @Inject EntityManager entityManager;

  /**
   * Converts a CityEntity to a CityView.
   *
   * @param c the CityEntity
   * @return the CityView
   */
  private static CityView toView(CityEntity c) {
    if (c == null) {
      return null;
    }
    return new CityView(c.getId(), c.getName(), c.getIbgeCode());
  }

  @Override
  public Optional<CityView> findOptionalById(UUID id) {
    if (id == null) {
      return Optional.empty();
    }
    var q =
        entityManager.createQuery(
            "select new com.pug.geo.infra.read.dtos.CityView("
                + "c.id, c.name, c.ibgeCode) "
                + "from CityEntity c where c.id = :id",
            CityView.class);
    q.setParameter("id", id);
    return q.getResultStream().findFirst();
  }

  @Override
  public Optional<CityView> findOptionalByIbgeCode(String ibgeCode) {
    if (ibgeCode == null || ibgeCode.isBlank()) {
      return Optional.empty();
    }
    var q =
        entityManager.createQuery(
            "select new com.pug.geo.infra.read.dtos.CityView("
                + "c.id, c.name, c.ibgeCode) "
                + "from CityEntity c where c.ibgeCode = :ibge",
            CityView.class);
    q.setParameter("ibge", ibgeCode);
    return q.getResultStream().findFirst();
  }

  @Override
  public List<CityView> listAllCities() {
    var q =
        entityManager.createQuery(
            "select new com.pug.geo.infra.read.dtos.CityView("
                + "c.id, c.name, c.ibgeCode) "
                + "from CityEntity c order by c.name asc",
            CityView.class);
    return q.getResultList();
  }

  @Override
  public List<CityView> searchByName(String key) {
    if (key == null || key.isBlank()) {
      return List.of();
    }

    String[] tokens = key.split("\\s+");
    SearchSession s = Search.session(entityManager);

    List<CityEntity> hits =
        s.search(CityEntity.class)
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

    List<CityView> out = new ArrayList<>(hits.size());
    for (CityEntity c : hits) {
      out.add(toView(c));
    }
    return out;
  }
}
