package com.pug.partner.infra.queries;

import com.pug.partner.presenter.dtos.EntityView;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** Implementation of EntityQueries using JPA constructor projections. */
@ApplicationScoped
public class EntityQueriesImpl implements EntityQueries {

  @Inject EntityManager em;

  @Override
  public Optional<EntityView> findById(UUID id) {
    var q =
        em.createQuery(
            """
                            select new com.pug.partner.presenter.dtos.EntityView(
                              e.id, e.cnpj, e.name, e.address,
                              new com.pug.geo.presenter.dtos.CityResponse(c.id, c.name, c.ibgeCode)
                            )
                            from EntityEntity e
                              join CityEntity c on c.id = e.cityId
                            where e.id = :id
                            """,
            EntityView.class);
    q.setParameter("id", id);
    return q.getResultList().stream().findFirst();
  }

  @Override
  public List<EntityView> listAll() {
    return em.createQuery(
            """
                            select new com.pug.partner.presenter.dtos.EntityView(
                              e.id, e.cnpj, e.name, e.address,
                              new com.pug.geo.presenter.dtos.CityResponse(c.id, c.name, c.ibgeCode)
                            )
                            from EntityEntity e
                              join CityEntity c on c.id = e.cityId
                            order by e.name
                            """,
            EntityView.class)
        .getResultList();
  }

  @Override
  public List<EntityView> listAllByCityId(UUID cityId) {
    var q =
        em.createQuery(
            """
                            select new com.pug.partner.presenter.dtos.EntityView(
                              e.id, e.cnpj, e.name, e.address,
                              new com.pug.geo.presenter.dtos.CityResponse(c.id, c.name, c.ibgeCode)
                            )
                            from EntityEntity e
                              join CityEntity c on c.id = e.cityId
                            where e.cityId = :cityId
                            order by e.name
                            """,
            EntityView.class);
    q.setParameter("cityId", cityId);
    return q.getResultList();
  }
}
