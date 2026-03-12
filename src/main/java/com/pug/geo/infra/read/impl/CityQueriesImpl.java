package com.pug.geo.infra.read.impl;

import com.pug.geo.infra.CityMapper;
import com.pug.geo.infra.persistence.CityEntity;
import com.pug.geo.infra.read.CityQueries;
import com.pug.geo.infra.read.dtos.CityView;
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

  /** {@inheritDoc} */
  @Override
  public Optional<CityView> findOptionalById(UUID id) {
    if (id == null) {
      return Optional.empty();
    }
    var q = entityManager.createQuery("from CityEntity c where c.id = :id", CityEntity.class);
    q.setParameter("id", id);
    return q.getResultStream().findFirst().map(CityMapper::toView);
  }

  /** {@inheritDoc} */
  @Override
  public Optional<CityView> findOptionalByIbgeCode(String ibgeCode) {
    if (StringUtils.isEmpty(ibgeCode)) {
      return Optional.empty();
    }
    var q =
        entityManager.createQuery("from CityEntity c where c.ibgeCode = :ibge", CityEntity.class);
    q.setParameter("ibge", ibgeCode);
    return q.getResultStream().findFirst().map(CityMapper::toView);
  }

  /** {@inheritDoc} */
  @Override
  public List<CityView> listAllByIds(List<UUID> ids) {
    if (ids == null || ids.isEmpty()) {
      return List.of();
    }
    var q =
        entityManager.createQuery(
            "from CityEntity c where c.id in :ids order by c.name asc", CityEntity.class);
    q.setParameter("ids", ids);
    return q.getResultList().stream().map(CityMapper::toView).toList();
  }

  /** {@inheritDoc} */
  @Override
  public List<CityView> listAllCities() {
    var q = entityManager.createQuery("from CityEntity c order by c.name asc", CityEntity.class);
    return q.getResultList().stream().map(CityMapper::toView).toList();
  }

  /** {@inheritDoc} */
  @Override
  public List<CityView> searchByName(String key) {
    List<CityEntity> hits = HibernateSearchUtils.searchByName(entityManager, CityEntity.class, key);

    if (hits.isEmpty()) {
      return List.of();
    }

    List<CityView> out = new ArrayList<>(hits.size());
    for (CityEntity c : hits) {
      out.add(CityMapper.toView(c));
    }
    return out;
  }
}
