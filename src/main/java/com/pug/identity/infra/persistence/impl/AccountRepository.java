package com.pug.identity.infra.persistence.impl;

import com.pug.identity.domain.Account;
import com.pug.identity.domain.IAccountRepository;
import com.pug.identity.infra.AccountMapper;
import com.pug.identity.infra.persistence.AccountEntity;
import com.pug.shared.utils.CollectionUtils;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementation of the AccountRepository using PanacheRepositoryBase for CRUD operations on
 * AccountEntity.
 */
@ApplicationScoped
public class AccountRepository
    implements IAccountRepository, PanacheRepositoryBase<AccountEntity, UUID> {

  @Transactional
  @Override
  public Account persist(Account entity) {
    if (entity == null) {
      return null;
    }
    AccountEntity e = AccountMapper.toEntity(entity);
    persistAndFlush(e);
    return AccountMapper.toDomain(e);
  }

  @Transactional
  @Override
  public List<Account> persistAll(Iterable<Account> entities) {
    if (CollectionUtils.isEmpty(entities)) {
      return List.of();
    }
    var batch = new ArrayList<AccountEntity>();
    for (Account d : entities) {
      if (d != null) {
        batch.add(AccountMapper.toEntity(d));
      }
    }
    if (batch.isEmpty()) {
      return List.of();
    }
    persist(batch);
    flush();
    return batch.stream().map(AccountMapper::toDomain).toList();
  }

  @Transactional
  @Override
  public void update(Account entity) {
    if (entity == null || entity.getId() == null) {
      return;
    }
    AccountEntity managed = findById(entity.getId());
    if (managed == null) {
      return;
    }
    AccountMapper.copy(entity, managed);
  }

  @Transactional
  @Override
  public long deleteByIds(Iterable<UUID> ids) {
    if (CollectionUtils.isEmpty(ids)) {
      return 0L;
    }
    long deleted = delete("id in ?1", ids);
    flush();
    getEntityManager().clear();
    return deleted;
  }

  @Override
  public Optional<Account> findOptionalById(UUID id) {
    return findByIdOptional(id).map(AccountMapper::toDomain);
  }

  @Override
  public List<Account> listAllAccounts() {
    return listAll().stream().map(AccountMapper::toDomain).toList();
  }

  @Override
  public List<UUID> listAllAccountUserIdsByIds(Iterable<UUID> ids) {
    if (CollectionUtils.isEmpty(ids)) {
      return List.of();
    }
    return find("id in ?1", ids).stream().map(AccountEntity::getUserId).toList();
  }

  @Override
  public List<UUID> findUserIdsWithAccountsExcluding(
      Iterable<UUID> excludeAccountIds, Iterable<UUID> userIds) {
    if (CollectionUtils.isEmpty(userIds)) {
      return List.of();
    }
    String query = "userId in ?1";
    if (!CollectionUtils.isEmpty(excludeAccountIds)) {
      query += " and id not in ?2";
      return find(query, userIds, excludeAccountIds).stream()
          .map(AccountEntity::getUserId)
          .distinct()
          .toList();
    } else {
      return find(query, userIds).stream().map(AccountEntity::getUserId).distinct().toList();
    }
  }

  @Override
  public boolean existsByEmail(String email) {
    return find("email", email).firstResultOptional().isPresent();
  }

  @Override
  public boolean existsAnyByEmailIn(Iterable<String> emails) {
    if (CollectionUtils.isEmpty(emails)) {
      return false;
    }
    return find("email in ?1", emails).firstResultOptional().isPresent();
  }

  @Override
  public boolean existsAnyByUserIdIn(Iterable<UUID> userIds) {
    if (CollectionUtils.isEmpty(userIds)) {
      return false;
    }
    return find("userId in ?1", userIds).firstResultOptional().isPresent();
  }
}
