package com.pug.partner.infra.queries;

import com.pug.partner.presenter.dtos.StaffView;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** Implementation of StaffQueries using JPA constructor projections. */
@ApplicationScoped
public class StaffQueriesImpl implements StaffQueries {

  @Inject EntityManager em;

  @Override
  public Optional<StaffView> findByUserId(UUID userId) {
    var q =
        em.createQuery(
            """
                    select new com.pug.partner.presenter.dtos.StaffView(
                      u.id, u.cpf, u.name, u.email, u.accountType, u.createdAt,
                      new com.pug.partner.presenter.dtos.EntityView(
                        e.id, e.cnpj, e.name, e.address,
                        new com.pug.geo.presenter.dtos.CityResponse(c.id, c.name, c.ibgeCode)
                      )
                    )
                    from StaffEntity s
                      join UserEntity u on u.id = s.userId
                      join EntityEntity e on e.id = s.entityId
                      join CityEntity c on c.id = e.cityId
                    where s.userId = :id
                    """,
            StaffView.class);
    q.setParameter("id", userId);
    return q.getResultList().stream().findFirst();
  }

  @Override
  public List<StaffView> listAll() {
    return em.createQuery(
            """
                select new com.pug.partner.presenter.dtos.StaffView(
                  u.id, u.cpf, u.name, u.email, u.accountType, u.createdAt,
                  new com.pug.partner.presenter.dtos.EntityView(
                    e.id, e.cnpj, e.name, e.address,
                    new com.pug.geo.presenter.dtos.CityResponse(c.id, c.name, c.ibgeCode)
                  )
                )
                from StaffEntity s
                  join UserEntity u on u.id = s.userId
                  join EntityEntity e on e.id = s.entityId
                  join CityEntity c on c.id = e.cityId
                order by u.name
                """,
            StaffView.class)
        .getResultList();
  }

  @Override
  public List<StaffView> listAllByEntityId(UUID entityId) {
    var q =
        em.createQuery(
            """
                        select new com.pug.partner.presenter.dtos.StaffView(
                          u.id, u.cpf, u.name, u.email, u.accountType, u.createdAt,
                          new com.pug.partner.presenter.dtos.EntityView(
                            e.id, e.cnpj, e.name, e.address,
                            new com.pug.geo.presenter.dtos.CityResponse(c.id, c.name, c.ibgeCode)
                          )
                        )
                        from StaffEntity s
                          join UserEntity u on u.id = s.userId
                          join EntityEntity e on e.id = s.entityId
                          join CityEntity c on c.id = e.cityId
                        where s.entityId = :entityId
                        order by u.name
                        """,
            StaffView.class);
    q.setParameter("entityId", entityId);
    return q.getResultList();
  }
}
