package com.pug.identity.infra.persistence;

import com.pug.identity.domain.Account;
import com.pug.identity.domain.AccountRepository;
import com.pug.identity.infra.AccountMapper;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementation of the AccountRepository using PanacheRepositoryBase for CRUD operations on
 * UsersEntity.
 */
@ApplicationScoped
public class AccountRepositoryImpl
    implements AccountRepository, PanacheRepositoryBase<AccountEntity, UUID> {

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
    if (entities == null || !entities.iterator().hasNext()) {
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
    if (!ids.iterator().hasNext()) {
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
  public boolean existsByEmail(String email) {
    return find("email", email).firstResultOptional().isPresent();
  }

  @Override
  public boolean existsAnyByEmailIn(Iterable<String> emails) {
    if (emails == null || !emails.iterator().hasNext()) {
      return false;
    }
    return find("email in ?1", emails).firstResultOptional().isPresent();
  }

  @Override
  public boolean existsAnyByUserIdIn(Iterable<UUID> userIds) {
    if (userIds == null || !userIds.iterator().hasNext()) {
      return false;
    }
    return find("userId in ?1", userIds).firstResultOptional().isPresent();
  }
}
