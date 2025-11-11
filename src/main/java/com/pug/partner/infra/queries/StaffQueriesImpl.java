package com.pug.partner.infra.queries;

import com.pug.partner.infra.read.StaffQueries;
import com.pug.partner.infra.read.dtos.StaffView;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.session.SearchSession;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementation of StaffQueries using JPQL constructor projections.
 */
@ApplicationScoped
@Transactional(Transactional.TxType.SUPPORTS)
public class StaffQueriesImpl implements StaffQueries {

  @Inject
  EntityManager em;

  private static final String SELECT_VIEW =
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
                  """;

  @Override
  public Optional<StaffView> findOptionalByUserId(UUID userId) {
    if (userId == null) {
      return Optional.empty();
    }
    var q = em.createQuery(SELECT_VIEW + " where s.userId = :uid", StaffView.class);
    q.setParameter("uid", userId);
    return q.getResultStream().findFirst();
  }

  @Override
  public Optional<StaffView> findOptionalByEmail(String email) {
    if (email == null || email.isBlank()) {
      return Optional.empty();
    }
    var q = em.createQuery(SELECT_VIEW + " where u.email = :email", StaffView.class);
    q.setParameter("email", email);
    return q.getResultStream().findFirst();
  }

  @Override
  public List<StaffView> listByCpf(String cpf) {
    if (cpf == null || cpf.isBlank()) {
      return List.of();
    }
    var q = em.createQuery(SELECT_VIEW + " where u.cpf = :cpf order by u.name asc", StaffView.class);
    q.setParameter("cpf", cpf);
    return q.getResultList();
  }

  @Override
  public List<StaffView> listAllStaff() {
    return em.createQuery(SELECT_VIEW + " order by u.name asc", StaffView.class).getResultList();
  }

  @Override
  public List<StaffView> listAllByEntityId(UUID entityId) {
    if (entityId == null) {
      return List.of();
    }
    var q =
      em.createQuery(
      SELECT_VIEW + " where e.id = :eid order by u.name asc", StaffView.class);
    q.setParameter("eid", entityId);
    return q.getResultList();
  }

  @Override
  public List<StaffView> searchByName(String key) {
    if (key == null || key.isBlank()) {
      return List.of();
    }

    String[] tokens = key.split("\\s+");
    SearchSession s = Search.session(em);

    List<com.pug.identity.infra.persistence.UserEntity> hits =
      s.search(com.pug.identity.infra.persistence.UserEntity.class)
        .where(f ->
          f.bool(b -> {
            b.should(f.wildcard().field("name_exact").matching(key + "*").boost(8f));
            b.should(f.wildcard().field("name_exact").matching("*" + key + "*").boost(6f));
            for (String t : tokens) {
              if (t.length() >= 3) {
                b.should(f.wildcard().field("name_exact").matching("*" + t + "*").boost(3f));
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

    List<UUID> userIds = new ArrayList<>(hits.size());
    for (var u : hits) {
      if (u.getId() != null) {
        userIds.add(u.getId());
      }
    }
    if (userIds.isEmpty()) {
      return List.of();
    }

    var q =
      em.createQuery(
      SELECT_VIEW + " where u.id in :ids order by u.name asc", StaffView.class);
    q.setParameter("ids", userIds);
    return q.getResultList();
  }
}
