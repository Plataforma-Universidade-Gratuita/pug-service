package com.pug.partner.infra.read.impl;

import com.pug.identity.infra.persistence.UserEntity;
import com.pug.partner.infra.read.StaffQueries;
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

import static com.pug.partner.infra.StaffMapper.toView;

/**
 * Implementation of the {@link StaffQueries} interface using JPA and Hibernate Search.
 * <p>
 * This application-scoped bean executes read-only operations for staff profiles.
 * Given the deeply nested structure of a staff member crossing multiple domains
 * (Staff -> Account -> User, Staff -> Entity -> City), these queries rely on explicitly
 * declared JPQL {@code JOIN} paths inside constructor expressions to build out the full
 * {@link StaffView} DTO without triggering N+1 select performance issues.
 */
@ApplicationScoped
@Transactional(Transactional.TxType.SUPPORTS)
public class StaffQueriesImpl implements StaffQueries {

  @Inject
  EntityManager em;

  private static final String SELECT_BASE =
          """
                  select new com.pug.partner.infra.read.dtos.StaffView(
                    new com.pug.identity.infra.read.dtos.AccountView(
                      acc.id,
                      new com.pug.identity.infra.read.dtos.UserView(
                      u.id, u.cpf, u.name, u.createdAt, u.updatedAt),
                      acc.email,
                      acc.accountType,
                      acc.createdAt,
                      acc.updatedAt
                    ),
                    new com.pug.partner.infra.read.dtos.EntityView(
                      e.id, e.cnpj, e.name, e.address,
                      new com.pug.geo.infra.read.dtos.CityView(c.id, c.name, c.ibgeCode),
                      e.createdAt, e.updatedAt
                    )
                  )
                  from StaffEntity s
                    join AccountEntity acc on acc.id = s.accountId
                    join UserEntity u on u.id = acc.userId
                    join EntityEntity e on e.id = s.entityId
                    join CityEntity c on c.id = e.cityId
                  """;

  private static final String ORDER_BY_PERSON_NAME_ASC = " order by u.name asc";

  /**
   * {@inheritDoc}
   */
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

  /**
   * {@inheritDoc}
   */
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

  /**
   * {@inheritDoc}
   */
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

  /**
   * {@inheritDoc}
   */
  @Override
  public List<StaffView> listAllStaff() {
    return em.createQuery(SELECT_BASE + ORDER_BY_PERSON_NAME_ASC, StaffView.class).getResultList();
  }

  /**
   * {@inheritDoc}
   */
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

  /**
   * {@inheritDoc}
   * <p>
   * To achieve a full-text search against the user's name, this method first resolves
   * the matching users via the search index, extracts their UUIDs, and executes a
   * subsequent query using the {@link StaffAcc} tuple projection to map the relationships
   * backwards across the Identity, Geo, and Partner domains.
   */
  @Override
  public List<StaffView> searchByName(String key) {
    List<UserEntity> userHits = HibernateSearchUtils.searchByName(em, UserEntity.class, key);
    if (userHits.isEmpty()) {
      return List.of();
    }

    List<UUID> userIds = userHits.stream().map(UserEntity::getId).toList();

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
      if (row.account() != null && row.account().getUserId() != null) {
        byUser.computeIfAbsent(row.account().getUserId(), k -> new ArrayList<>()).add(row);
      }
    }

    List<StaffView> out = new ArrayList<>();
    for (UserEntity u : userHits) {
      List<StaffAcc> pairs = byUser.get(u.getId());
      if (pairs == null) {
        continue;
      }
      for (StaffAcc row : pairs) {
        if (row.staff() != null
                && row.account() != null
                && row.entity() != null
                && row.city() != null) {
          out.add(toView(row.account(), row.entity(), row.city(), u));
        }
      }
    }
    return out;
  }
}