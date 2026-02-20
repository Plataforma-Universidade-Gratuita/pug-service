package com.pug.identity.infra.persistence.impl;

import com.pug.identity.domain.Account;
import com.pug.identity.domain.AccountRepository;
import com.pug.identity.infra.AccountMapper;
import com.pug.identity.infra.persistence.AccountEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementation of the AccountRepository using PanacheRepositoryBase for CRUD operations on
 * AccountEntity.
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
  public boolean deleteById(UUID id) {
    if (id == null) {
      return false;
    }
    var result = PanacheRepositoryBase.super.deleteById(id);
    flush();
    return result;
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
  public long countAllAccountsByUserId(UUID userId) {
    if (userId == null) {
      return 0;
    }
    return count("userId", userId);
  }

  @Override
  public boolean existsByEmail(String email) {
    return find("email", email).firstResultOptional().isPresent();
  }
}
