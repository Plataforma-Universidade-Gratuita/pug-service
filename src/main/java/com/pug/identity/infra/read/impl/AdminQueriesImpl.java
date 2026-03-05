package com.pug.identity.infra.read.impl;

import static com.pug.identity.infra.AdminMapper.toView;

import com.pug.identity.infra.persistence.UserEntity;
import com.pug.identity.infra.read.AdminQueries;
import com.pug.identity.infra.read.dtos.AdminAcc;
import com.pug.identity.infra.read.dtos.AdminView;
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
 * Implementation of the {@link AdminQueries} interface using JPA and Hibernate Search.
 *
 * <p>This application-scoped bean executes read-only operations for administrative profiles. Given
 * the deeply nested structure of an administrator (Admin -> Account -> User), these queries rely on
 * explicitly declared JPQL {@code JOIN} paths inside constructor expressions to build out the full,
 * nested {@link AdminView} DTO without triggering N+1 select performance issues.
 */
@ApplicationScoped
@Transactional(Transactional.TxType.SUPPORTS)
public class AdminQueriesImpl implements AdminQueries {

  @Inject EntityManager em;

  private static final String SELECT_BASE =
      """
                  select new com.pug.identity.infra.read.dtos.AdminView(
                    new com.pug.identity.infra.read.dtos.AccountView(
                      acc.id,
                      new com.pug.identity.infra.read.dtos.UserView(
                      u.id, u.cpf, u.name, u.createdAt, u.updatedAt),
                      acc.email,
                      acc.accountType,
                      acc.createdAt,
                      acc.updatedAt,
                      acc.active
                    ),
                    a.grantedAt,
                    a.campus
                  )
                  from AdminEntity a
                    join AccountEntity acc on acc.id = a.accountId
                    join UserEntity u on u.id = acc.userId
                  """;

  private static final String ORDER_BY_PERSON_NAME_ASC = " order by u.name asc";

  /** {@inheritDoc} */
  @Override
  public Optional<AdminView> findOptionalById(UUID accountId) {
    if (accountId == null) {
      return Optional.empty();
    }
    var q = em.createQuery(SELECT_BASE + " where a.accountId = :id", AdminView.class);
    q.setParameter("id", accountId);
    return q.getResultStream().findFirst();
  }

  /** {@inheritDoc} */
  @Override
  public Optional<AdminView> findOptionalByEmail(String email) {
    if (StringUtils.isEmpty(email)) {
      return Optional.empty();
    }
    var q = em.createQuery(SELECT_BASE + " where acc.email = :email", AdminView.class);
    q.setParameter("email", email);
    return q.getResultStream().findFirst();
  }

  /** {@inheritDoc} */
  @Override
  public List<AdminView> listAllAdmins() {
    return em.createQuery(SELECT_BASE + ORDER_BY_PERSON_NAME_ASC, AdminView.class).getResultList();
  }

  /** {@inheritDoc} */
  @Override
  public List<AdminView> listByCpf(String cpf) {
    if (StringUtils.isEmpty(cpf)) {
      return List.of();
    }
    var q =
        em.createQuery(
            SELECT_BASE + " where u.cpf = :cpf" + ORDER_BY_PERSON_NAME_ASC, AdminView.class);
    q.setParameter("cpf", cpf);
    return q.getResultList();
  }

  /**
   * {@inheritDoc}
   *
   * <p>To achieve a full-text search against the user's name, this method first resolves the
   * matching users via the search index, extracts their UUIDs, and executes a subsequent query
   * using the {@link AdminAcc} tuple projection to map the relationships backwards to the
   * administrator profile.
   */
  @Override
  public List<AdminView> searchByName(String key) {
    List<UserEntity> userHits = HibernateSearchUtils.searchByName(em, UserEntity.class, key);
    if (userHits.isEmpty()) {
      return List.of();
    }

    List<UUID> userIds = userHits.stream().map(UserEntity::getId).toList();

    var rows =
        em.createQuery(
                """
                  select new com.pug.identity.infra.read.dtos.AdminAcc(a, acc)
                  from AdminEntity a join AccountEntity acc on acc.id = a.accountId
                  where acc.userId in :ids
                  """,
                AdminAcc.class)
            .setParameter("ids", userIds)
            .getResultList();

    Map<UUID, List<AdminAcc>> byUser = new HashMap<>();
    for (AdminAcc row : rows) {
      byUser.computeIfAbsent(row.account().getUserId(), k -> new ArrayList<>()).add(row);
    }

    List<AdminView> out = new ArrayList<>();
    for (UserEntity u : userHits) {
      List<AdminAcc> pairs = byUser.get(u.getId());
      if (pairs == null) {
        continue;
      }
      for (AdminAcc row : pairs) {
        out.add(toView(row.admin(), row.account(), u));
      }
    }
    return out;
  }
}
