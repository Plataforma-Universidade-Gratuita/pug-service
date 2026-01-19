package com.pug.partner.infra.queries;

import com.pug.geo.infra.persistence.CityEntity;
import com.pug.geo.infra.read.dtos.CityView;
import com.pug.identity.infra.persistence.AccountEntity;
import com.pug.identity.infra.persistence.UserEntity;
import com.pug.identity.infra.read.dtos.AccountView;
import com.pug.identity.infra.read.dtos.UserView;
import com.pug.partner.infra.persistence.EntityEntity;
import com.pug.partner.infra.read.StaffQueries;
import com.pug.partner.infra.read.dtos.EntityView;
import com.pug.partner.infra.read.dtos.StaffAcc;
import com.pug.partner.infra.read.dtos.StaffView;
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

/** Implementation of StaffQueries using JPA EntityManager. */
@ApplicationScoped
@Transactional(Transactional.TxType.SUPPORTS)
public class StaffQueriesImpl implements StaffQueries {

  @Inject EntityManager em;

  private static final String SELECT_BASE =
      """
                  select new com.pug.partner.infra.read.dtos.StaffView(
                    new com.pug.identity.infra.read.dtos.AccountView(
                      acc.id,
                      new com.pug.identity.infra.read.dtos.UserView(u.id, u.cpf, u.name, u.createdAt),
                      acc.email,
                      acc.accountType,
                      acc.createdAt
                    ),
                    new com.pug.partner.infra.read.dtos.EntityView(
                      e.id, e.cnpj, e.name, e.address,
                      new com.pug.geo.infra.read.dtos.CityView(c.id, c.name, c.ibgeCode)
                    )
                  )
                  from StaffEntity s
                    join AccountEntity acc on acc.id = s.accountId
                    join UserEntity u on u.id = acc.userId
                    join EntityEntity e on e.id = s.entityId
                    join CityEntity c on c.id = e.cityId
                  """;

  private static final String ORDER_BY_PERSON_NAME_ASC = " order by u.name asc";

  @Override
  public Optional<StaffView> findOptionalById(UUID accountId) {
    if (accountId == null) {
      return Optional.empty();
    }
    var q =
        em.createQuery(SELECT_BASE + " where s.accountId = :id", StaffView.class)
            .setParameter("id", accountId);
    return q.getResultStream().findFirst();
  }

  @Override
  public Optional<StaffView> findOptionalByEmail(String email) {
    if (StringUtils.isEmpty(email)) {
      return Optional.empty();
    }
    var q =
        em.createQuery(SELECT_BASE + " where acc.email = :email", StaffView.class)
            .setParameter("email", email);
    return q.getResultStream().findFirst();
  }

  @Override
  public List<StaffView> listByCpf(String cpf) {
    if (StringUtils.isEmpty(cpf)) {
      return List.of();
    }
    var q =
        em.createQuery(
                SELECT_BASE + " where u.cpf = :cpf" + ORDER_BY_PERSON_NAME_ASC, StaffView.class)
            .setParameter("cpf", cpf);
    return q.getResultList();
  }

  @Override
  public List<StaffView> listAllStaff() {
    return em.createQuery(SELECT_BASE + ORDER_BY_PERSON_NAME_ASC, StaffView.class).getResultList();
  }

  @Override
  public List<StaffView> listAllByEntityId(UUID entityId) {
    if (entityId == null) {
      return List.of();
    }
    var q =
        em.createQuery(
                SELECT_BASE + " where e.id = :eid" + ORDER_BY_PERSON_NAME_ASC, StaffView.class)
            .setParameter("eid", entityId);
    return q.getResultList();
  }

  @Override
  public List<StaffView> searchByName(String key) {
    List<UserEntity> personHits = HibernateSearchUtils.searchByName(em, UserEntity.class, key);
    if (personHits.isEmpty()) {
      return List.of();
    }

    List<UUID> userIds = personHits.stream().map(UserEntity::getId).toList();

    var rows =
        em.createQuery(
                """
                                    select new com.pug.partner.infra.read.dtos.StaffAcc(s, acc, e, c)
                                    from StaffEntity s
                                      join AccountEntity acc on acc.id = s.accountId
                                      join EntityEntity e on e.id = s.entityId
                                      join CityEntity c on c.id = e.cityId
                                    where acc.userId in :ids
                                    """,
                StaffAcc.class)
            .setParameter("ids", userIds)
            .getResultList();

    Map<UUID, List<StaffAcc>> byUser = new HashMap<>();
    for (StaffAcc row : rows) {
      byUser.computeIfAbsent(row.account().getUserId(), k -> new ArrayList<>()).add(row);
    }

    List<StaffView> out = new ArrayList<>();
    for (UserEntity u : personHits) {
      List<StaffAcc> pairs = byUser.get(u.getId());
      if (pairs == null) {
        continue;
      }
      for (StaffAcc row : pairs) {
        out.add(toView(row.account(), row.entity(), row.city(), u));
      }
    }
    return out;
  }

  private static StaffView toView(AccountEntity acc, EntityEntity e, CityEntity c, UserEntity u) {
    return new StaffView(
        new AccountView(
            acc.getId(),
            new UserView(u.getId(), u.getCpf(), u.getName(), u.getCreatedAt()),
            acc.getEmail(),
            acc.getAccountType(),
            acc.getCreatedAt()),
        new EntityView(
            e.getId(),
            e.getCnpj(),
            e.getName(),
            e.getAddress(),
            new CityView(c.getId(), c.getName(), c.getIbgeCode())));
  }
}
