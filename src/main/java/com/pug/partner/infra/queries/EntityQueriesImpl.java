package com.pug.partner.infra.queries;

import com.pug.geo.infra.read.dtos.CityView;
import com.pug.partner.infra.persistence.EntityEntity;
import com.pug.partner.infra.read.EntityQueries;
import com.pug.partner.infra.read.dtos.EntityView;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.session.SearchSession;

/** Implementation of EntityQueries using JPA and Hibernate Search. */
@ApplicationScoped
@Transactional(Transactional.TxType.SUPPORTS)
public class EntityQueriesImpl implements EntityQueries {

  @Inject EntityManager em;

  @Override
  public Optional<EntityView> findOptionalById(UUID id) {
    if (id == null) {
      return Optional.empty();
    }

    var q =
        em.createQuery(
            """
                select new com.pug.partner.infra.read.dtos.EntityView(
                  e.id, e.cnpj, e.name, e.address,
                  new com.pug.geo.infra.read.dtos.CityView(c.id, c.name, c.ibgeCode)
                )
                from EntityEntity e
                  join CityEntity c on c.id = e.cityId
                where e.id = :id
                """,
            EntityView.class);
    q.setParameter("id", id);
    return q.getResultStream().findFirst();
  }

  @Override
  public Optional<EntityView> findOptionalByCnpj(String cnpj) {
    if (cnpj == null || cnpj.isBlank()) {
      return Optional.empty();
    }

    var q =
        em.createQuery(
            """
                select new com.pug.partner.infra.read.dtos.EntityView(
                  e.id, e.cnpj, e.name, e.address,
                  new com.pug.geo.infra.read.dtos.CityView(c.id, c.name, c.ibgeCode)
                )
                from EntityEntity e
                  join CityEntity c on c.id = e.cityId
                where e.cnpj = :cnpj
                """,
            EntityView.class);
    q.setParameter("cnpj", cnpj);
    return q.getResultStream().findFirst();
  }

  @Override
  public List<EntityView> listAllEntities() {
    return em.createQuery(
            """
                select new com.pug.partner.infra.read.dtos.EntityView(
                  e.id, e.cnpj, e.name, e.address,
                  new com.pug.geo.infra.read.dtos.CityView(c.id, c.name, c.ibgeCode)
                )
                from EntityEntity e
                  join CityEntity c on c.id = e.cityId
                order by e.name asc
                """,
            EntityView.class)
        .getResultList();
  }

  @Override
  public List<EntityView> listAllByCityId(UUID cityId) {
    if (cityId == null) {
      return List.of();
    }

    var q =
        em.createQuery(
            """
                select new com.pug.partner.infra.read.dtos.EntityView(
                  e.id, e.cnpj, e.name, e.address,
                  new com.pug.geo.infra.read.dtos.CityView(c.id, c.name, c.ibgeCode)
                )
                from EntityEntity e
                  join CityEntity c on c.id = e.cityId
                where e.cityId = :cityId
                order by e.name asc
                """,
            EntityView.class);
    q.setParameter("cityId", cityId);
    return q.getResultList();
  }

  @Override
  public List<EntityView> searchByName(String key) {
    if (key == null || key.isBlank()) {
      return List.of();
    }

    String[] tokens = key.split("\\s+");
    SearchSession s = Search.session(em);

    List<EntityEntity> hits =
        s.search(EntityEntity.class)
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

    var cityIds = hits.stream().map(EntityEntity::getCityId).distinct().toList();
    var cityMap =
        em
            .createQuery(
                """
                    select new com.pug.geo.infra.read.dtos.CityView(c.id, c.name, c.ibgeCode)
                    from CityEntity c
                    where c.id in :ids
                    """,
                CityView.class)
            .setParameter("ids", cityIds)
            .getResultList()
            .stream()
            .collect(Collectors.toMap(CityView::id, Function.identity()));

    List<EntityView> out = new ArrayList<>(hits.size());
    for (EntityEntity e : hits) {
      CityView city = cityMap.get(e.getCityId());
      out.add(new EntityView(e.getId(), e.getCnpj(), e.getName(), e.getAddress(), city));
    }
    return out;
  }
}
