package com.pug.identity.infra.read.impl;

import com.pug.identity.infra.persistence.AccountEntity;
import com.pug.identity.infra.persistence.UserEntity;
import com.pug.identity.infra.read.AccountQueries;
import com.pug.identity.infra.read.dtos.AccountView;
import com.pug.shared.infra.search.HibernateSearchUtils;
import com.pug.shared.utils.StringUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.pug.identity.infra.AccountMapper.toView;

/**
 * Implementation of the {@link AccountQueries} interface using JPA and Hibernate Search.
 * <p>
 * This application-scoped bean handles read-only queries for accounts. Because an
 * account inherently belongs to a user, the JPQL queries utilize constructor expressions
 * that implicitly join the {@link AccountEntity} and {@link UserEntity} tables to assemble
 * a fully populated {@link AccountView} in a single database round-trip.
 */
@ApplicationScoped
@Transactional(Transactional.TxType.SUPPORTS)
public class AccountQueriesImpl implements AccountQueries {

  @Inject
  EntityManager entityManager;

  private static final String SELECT_BASE =
          """
                  select new com.pug.identity.infra.read.dtos.AccountView(
                    a.id,
                    new com.pug.identity.infra.read.dtos.UserView(u.id, u.cpf, u.name, u.createdAt, u.updatedAt),
                    a.email,
                    a.accountType,
                    a.createdAt,
                    a.updatedAt
                  )
                  from AccountEntity a, UserEntity u
                  where u.id = a.userId
                  """;

  private static final String ORDER_BY_NAME_ASC = " order by u.name asc";

  /**
   * {@inheritDoc}
   */
  @Override
  public Optional<AccountView> findOptionalById(UUID id) {
    if (id == null) {
      return Optional.empty();
    }
    var q = entityManager.createQuery(SELECT_BASE + " and a.id = :id", AccountView.class);
    q.setParameter("id", id);
    return q.getResultStream().findFirst();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Optional<AccountView> findOptionalByEmail(String email) {
    if (StringUtils.isEmpty(email)) {
      return Optional.empty();
    }
    var q = entityManager.createQuery(SELECT_BASE + " and a.email = :email", AccountView.class);
    q.setParameter("email", email);
    return q.getResultStream().findFirst();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<AccountView> listAllAccounts() {
    return entityManager
            .createQuery(SELECT_BASE + ORDER_BY_NAME_ASC, AccountView.class)
            .getResultList();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<AccountView> listByCpf(String cpf) {
    if (StringUtils.isEmpty(cpf)) {
      return List.of();
    }
    var q = entityManager.createQuery(SELECT_BASE + " and u.cpf = :cpf", AccountView.class);
    q.setParameter("cpf", cpf);
    return q.getResultList();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<AccountView> searchByName(String key) {
    List<UserEntity> userHits =
            HibernateSearchUtils.searchByName(entityManager, UserEntity.class, key);

    if (userHits.isEmpty()) {
      return List.of();
    }

    List<UUID> userIds = userHits.stream().map(UserEntity::getId).toList();

    List<AccountEntity> accountEntities =
            entityManager
                    .createQuery("from AccountEntity a where a.userId in :userIds", AccountEntity.class)
                    .setParameter("userIds", userIds)
                    .getResultList();

    Map<UUID, UserEntity> userEntityMap =
            userHits.stream().collect(Collectors.toMap(UserEntity::getId, user -> user));

    List<AccountView> out = new ArrayList<>();
    for (AccountEntity accountEntity : accountEntities) {
      UserEntity userEntity = userEntityMap.get(accountEntity.getUserId());
      if (userEntity != null) {
        out.add(toView(accountEntity, userEntity));
      }
    }
    return out;
  }
}