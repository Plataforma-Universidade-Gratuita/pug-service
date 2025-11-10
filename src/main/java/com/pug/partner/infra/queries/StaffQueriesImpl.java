package com.pug.partner.infra.queries;

import com.pug.partner.infra.read.StaffQueries;
import com.pug.partner.infra.read.dtos.StaffView;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** Implementation of StaffQueries using JPQL constructor projections. */
@ApplicationScoped
@Transactional(Transactional.TxType.SUPPORTS)
public class StaffQueriesImpl implements StaffQueries {

  @Inject EntityManager em;

  @Override
  public Optional<StaffView> findOptionalByUserId(UUID userId) {
    if (userId == null) {
      return Optional.empty();
    }
    var q =
        em.createQuery(
            """
                            select new com.pug.partner.infra.read.dtos.StaffView(
                              new com.pug.identity.infra.read.dtos.UserView(
                                u.id, u.cpf, u.name, u.email, u.accountType, u.createdAt
                              ),
                              new com.pug.partner.infra.read.dtos.EntityView(
                                e.id, e.cnpj, e.name, e.address,
                                new com.pug.geo.infra.read.dtos.CityView(c.id, c.name, c.ibgeCode)
                              )
                            )
                            from StaffEntity s
                              join UserEntity u on u.id = s.userId
                              join EntityEntity e on e.id = s.entityId
                              join CityEntity c on c.id = e.cityId
                            where s.userId = :uid
                            """,
            StaffView.class);
    q.setParameter("uid", userId);
    return q.getResultStream().findFirst();
  }

  @Override
  public List<StaffView> listAllStaff() {
    return em.createQuery(
            """
                            select new com.pug.partner.infra.read.dtos.StaffView(
                              new com.pug.identity.infra.read.dtos.UserView(
                                u.id, u.cpf, u.name, u.email, u.accountType, u.createdAt
                              ),
                              new com.pug.partner.infra.read.dtos.EntityView(
                                e.id, e.cnpj, e.name, e.address,
                                new com.pug.geo.infra.read.dtos.CityView(c.id, c.name, c.ibgeCode)
                              )
                            )
                            from StaffEntity s
                              join UserEntity u on u.id = s.userId
                              join EntityEntity e on e.id = s.entityId
                              join CityEntity c on c.id = e.cityId
                            order by u.name asc
                            """,
            StaffView.class)
        .getResultList();
  }

  @Override
  public List<StaffView> listAllByEntityId(UUID entityId) {
    if (entityId == null) {
      return List.of();
    }
    var q =
        em.createQuery(
            """
                            select new com.pug.partner.infra.read.dtos.StaffView(
                              new com.pug.identity.infra.read.dtos.UserView(
                                u.id, u.cpf, u.name, u.email, u.accountType, u.createdAt
                              ),
                              new com.pug.partner.infra.read.dtos.EntityView(
                                e.id, e.cnpj, e.name, e.address,
                                new com.pug.geo.infra.read.dtos.CityView(c.id, c.name, c.ibgeCode)
                              )
                            )
                            from StaffEntity s
                              join UserEntity u on u.id = s.userId
                              join EntityEntity e on e.id = s.entityId
                              join CityEntity c on c.id = e.cityId
                            where e.id = :eid
                            order by u.name asc
                            """,
            StaffView.class);
    q.setParameter("eid", entityId);
    return q.getResultList();
  }
}
