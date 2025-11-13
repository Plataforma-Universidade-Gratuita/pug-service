package com.pug.partner.infra.queries;

import com.pug.geo.infra.read.dtos.CityView;
import com.pug.partner.infra.persistence.EntityEntity;
import com.pug.partner.infra.read.EntityQueries;
import com.pug.partner.infra.read.dtos.EntityView;
import com.pug.shared.infra.search.HibernateSearchUtils;
import com.pug.shared.utils.StringUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementation of EntityQueries using JPA and Hibernate Search.
 */
@ApplicationScoped
@Transactional(Transactional.TxType.SUPPORTS)
public class EntityQueriesImpl implements EntityQueries {

  @Inject
  EntityManager em;

  private static final String SELECT_BASE =
          """
                  select new com.pug.partner.infra.read.dtos.EntityView(
                    e.id, e.cnpj, e.name, e.address,
                    new com.pug.geo.infra.read.dtos.CityView(c.id, c.name, c.ibgeCode)
                  )
                  from EntityEntity e
                    join CityEntity c on c.id = e.cityId
                  """;

  private static final String ORDER_BY_NAME_ASC = " order by e.name asc";

  @Override
  public Optional<EntityView> findOptionalById(UUID id) {
    if (id == null) {
      return Optional.empty();
    }

    var q =
            em.createQuery(SELECT_BASE + " where e.id = :id", EntityView.class).setParameter("id", id);
    return q.getResultStream().findFirst();
  }

  @Override
  public Optional<EntityView> findOptionalByCnpj(String cnpj) {
    if (StringUtils.isEmpty(cnpj)) {
      return Optional.empty();
    }

    var q =
            em.createQuery(SELECT_BASE + " where e.cnpj = :cnpj", EntityView.class)
                    .setParameter("cnpj", cnpj);
    return q.getResultStream().findFirst();
  }

  @Override
  public List<EntityView> listAllEntities() {
    return em.createQuery(SELECT_BASE + ORDER_BY_NAME_ASC, EntityView.class).getResultList();
  }

  @Override
  public List<EntityView> listAllByCityId(UUID cityId) {
    if (cityId == null) {
      return List.of();
    }

    var q =
            em.createQuery(
                            SELECT_BASE + " where e.cityId = :cityId" + ORDER_BY_NAME_ASC, EntityView.class)
                    .setParameter("cityId", cityId);
    return q.getResultList();
  }

  @Override
  public List<EntityView> searchByName(String key) {
    List<EntityEntity> hits = HibernateSearchUtils.searchByName(em, EntityEntity.class, key);
    if (hits.isEmpty()) {
      return List.of();
    }

    List<UUID> cityIds = hits.stream().map(EntityEntity::getCityId).distinct().toList();

    List<CityView> cities =
            em.createQuery(
                            """
                                    select new com.pug.geo.infra.read.dtos.CityView(c.id, c.name, c.ibgeCode)
                                    from CityEntity c
                                    where c.id in :ids
                                    """,
                            CityView.class)
                    .setParameter("ids", cityIds)
                    .getResultList();

    Map<UUID, CityView> cityMap = new HashMap<>();
    for (CityView c : cities) {
      cityMap.put(c.id(), c);
    }

    List<EntityView> out = new ArrayList<>(hits.size());
    for (EntityEntity e : hits) {
      CityView city = cityMap.get(e.getCityId());
      out.add(new EntityView(e.getId(), e.getCnpj(), e.getName(), e.getAddress(), city));
    }
    return out;
  }
}
