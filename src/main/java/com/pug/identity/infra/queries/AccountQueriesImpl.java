package com.pug.identity.infra.queries;

import com.pug.identity.infra.persistence.AccountEntity;
import com.pug.identity.infra.persistence.UserEntity;
import com.pug.identity.infra.read.AccountQueries;
import com.pug.identity.infra.read.dtos.AccountView;
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

@ApplicationScoped
@Transactional(Transactional.TxType.SUPPORTS)
public class AccountQueriesImpl implements AccountQueries {

  @Inject EntityManager entityManager;

  private static final String SELECT_BASE =
      """
      select new com.pug.identity.infra.read.dtos.AccountView(
        u.id,
        new com.pug.identity.infra.read.dtos.UserView(p.id, p.cpf, p.name, p.createdAt),
        u.email,
        u.accountType,
        u.createdAt
      )
      from AccountEntity u, UserEntity p
      where p.id = u.userId
      """;

  private static final String ORDER_BY_NAME_ASC = " order by p.name asc";

  private static AccountView toView(AccountEntity u, UserEntity p) {
    if (u == null || p == null) {
      return null;
    }
    return new AccountView(
        u.getId(),
        new UserView(p.getId(), p.getCpf(), p.getName(), p.getCreatedAt()),
        u.getEmail(),
        u.getAccountType(),
        u.getCreatedAt());
  }

  @Override
  public Optional<AccountView> findOptionalById(UUID id) {
    if (id == null) {
      return Optional.empty();
    }
    var q = entityManager.createQuery(SELECT_BASE + " and u.id = :id", AccountView.class);
    q.setParameter("id", id);
    return q.getResultStream().findFirst();
  }

  @Override
  public Optional<AccountView> findOptionalByEmail(String email) {
    if (StringUtils.isEmpty(email)) {
      return Optional.empty();
    }
    var q = entityManager.createQuery(SELECT_BASE + " and u.email = :email", AccountView.class);
    q.setParameter("email", email);
    return q.getResultStream().findFirst();
  }

  @Override
  public List<AccountView> listAllAccounts() {
    return entityManager
        .createQuery(SELECT_BASE + ORDER_BY_NAME_ASC, AccountView.class)
        .getResultList();
  }

  @Override
  public List<AccountView> listByCpf(String cpf) {
    if (StringUtils.isEmpty(cpf)) {
      return List.of();
    }
    var q = entityManager.createQuery(SELECT_BASE + " and p.cpf = :cpf", AccountView.class);
    q.setParameter("cpf", cpf);
    return q.getResultList();
  }

  @Override
  public List<AccountView> searchByName(String key) {
    List<UserEntity> personHits =
        HibernateSearchUtils.searchByName(entityManager, UserEntity.class, key);

    if (personHits.isEmpty()) {
      return List.of();
    }

    List<UUID> userIds = personHits.stream().map(UserEntity::getId).toList();

    List<AccountEntity> users =
        entityManager
            .createQuery("from AccountEntity u where u.userId in :ids", AccountEntity.class)
            .setParameter("ids", userIds)
            .getResultList();

    Map<UUID, List<AccountEntity>> byPerson = new HashMap<>();
    for (AccountEntity u : users) {
      byPerson.computeIfAbsent(u.getUserId(), k -> new ArrayList<>()).add(u);
    }

    List<AccountView> out = new ArrayList<>();
    for (UserEntity p : personHits) {
      List<AccountEntity> us = byPerson.get(p.getId());
      if (us == null) {
        continue;
      }
      for (AccountEntity u : us) {
        out.add(toView(u, p));
      }
    }
    return out;
  }
}
