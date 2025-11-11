package com.pug.geo.infra.queries;

import com.pug.geo.infra.persistence.CityEntity;
import com.pug.geo.infra.read.CityQueries;
import com.pug.geo.infra.read.dtos.CityView;
import com.pug.identity.infra.persistence.UserEntity;
import com.pug.shared.infra.search.HibernateSearchUtils;
import com.pug.shared.utils.StringUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
    if (StringUtils.isEmpty(ibgeCode)) {
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
    List<CityEntity> hits = HibernateSearchUtils.searchByName(entityManager, CityEntity.class, key);

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
