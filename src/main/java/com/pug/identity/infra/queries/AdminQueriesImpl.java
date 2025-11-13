package com.pug.identity.infra.queries;

import com.pug.identity.infra.persistence.AccountEntity;
import com.pug.identity.infra.persistence.AdminEntity;
import com.pug.identity.infra.persistence.UserEntity;
import com.pug.identity.infra.read.AdminQueries;
import com.pug.identity.infra.read.dtos.AccountView;
import com.pug.identity.infra.read.dtos.AdminAcc;
import com.pug.identity.infra.read.dtos.AdminView;
import com.pug.identity.infra.read.dtos.UserView;
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

/** Implementation of AdminQueries using JPA EntityManager. */
@ApplicationScoped
@Transactional(Transactional.TxType.SUPPORTS)
public class AdminQueriesImpl implements AdminQueries {

  @Inject EntityManager em;

  private static final String SELECT_BASE =
      """
                  select new com.pug.identity.infra.read.dtos.AdminView(
                    new com.pug.identity.infra.read.dtos.AccountView(
                      acc.id,
                      new com.pug.identity.infra.read.dtos.UserView(u.id, u.cpf, u.name, u.createdAt),
                      acc.email,
                      acc.accountType,
                      acc.createdAt
                    ),
                    a.grantedAt
                  )
                  from AdminEntity a
                    join AccountEntity acc on acc.id = a.accountId
                    join UserEntity u on u.id = acc.userId
                  """;

  private static final String ORDER_BY_PERSON_NAME_ASC = " order by u.name asc";

  @Override
  public Optional<AdminView> findOptionalById(UUID accountId) {
    if (accountId == null) {
      return Optional.empty();
    }
    var q = em.createQuery(SELECT_BASE + " where a.accountId = :id", AdminView.class);
    q.setParameter("id", accountId);
    return q.getResultStream().findFirst();
  }

  @Override
  public Optional<AdminView> findOptionalByEmail(String email) {
    if (StringUtils.isEmpty(email)) {
      return Optional.empty();
    }
    var q = em.createQuery(SELECT_BASE + " where acc.email = :email", AdminView.class);
    q.setParameter("email", email);
    return q.getResultStream().findFirst();
  }

  @Override
  public List<AdminView> listAllAdmins() {
    return em.createQuery(SELECT_BASE + ORDER_BY_PERSON_NAME_ASC, AdminView.class).getResultList();
  }

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

  @Override
  public List<AdminView> searchByName(String key) {
    List<UserEntity> personHits = HibernateSearchUtils.searchByName(em, UserEntity.class, key);
    if (personHits.isEmpty()) {
      return List.of();
    }

    List<UUID> userIds = personHits.stream().map(UserEntity::getId).toList();

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
    for (UserEntity u : personHits) {
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

  private static AdminView toView(AdminEntity a, AccountEntity acc, UserEntity u) {
    return new AdminView(
        new AccountView(
            acc.getId(),
            new UserView(u.getId(), u.getCpf(), u.getName(), u.getCreatedAt()),
            acc.getEmail(),
            acc.getAccountType(),
            acc.getCreatedAt()),
        a.getGrantedAt());
  }
}
