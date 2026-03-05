package com.pug.identity.infra.persistence.impl;

import com.pug.identity.domain.Account;
import com.pug.identity.domain.AccountRepository;
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
 * Implementation of the {@link AccountRepository} utilizing Hibernate ORM with Panache.
 *
 * <p>This application-scoped bean bridges the pure domain repository interface with the underlying
 * database infrastructure. It handles standard CRUD operations as well as custom JPQL queries for
 * evaluating relational states (e.g., identifying orphaned users).
 */
@ApplicationScoped
public class AccountRepositoryImpl
    implements AccountRepository, PanacheRepositoryBase<AccountEntity, UUID> {

  /** {@inheritDoc} */
  @Override
  public long countAllAccountsByUserId(UUID userId) {
    if (userId == null) {
      return 0;
    }
    return count("userId", userId);
  }

  /** {@inheritDoc} */
  @Transactional
  @Override
  public long deleteAllByIds(List<UUID> ids) {
    if (CollectionUtils.isEmpty(ids)) {
      return 0;
    }
    return delete("id in ?1", ids);
  }

  /** {@inheritDoc} */
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

  /** {@inheritDoc} */
  @Override
  public boolean existsByEmail(String email) {
    return find("email", email).firstResultOptional().isPresent();
  }

  /** {@inheritDoc} */
  @Override
  public List<UUID> findAllOrphanUserIdsByUserIds(List<UUID> userIds) {
    if (CollectionUtils.isEmpty(userIds)) {
      return List.of();
    }

    List<UUID> usedUserIds =
        getEntityManager()
            .createQuery("SELECT a.userId FROM AccountEntity a WHERE a.userId IN :ids", UUID.class)
            .setParameter("ids", userIds)
            .getResultList();

    List<UUID> orphans = new ArrayList<>(userIds);
    orphans.removeAll(usedUserIds);
    return orphans;
  }

  /** {@inheritDoc} */
  @Override
  public Optional<Account> findOptionalById(UUID id) {
    return findByIdOptional(id).map(AccountMapper::toDomain);
  }

  /** {@inheritDoc} */
  @Override
  public List<UUID> findUserIdsByIds(List<UUID> ids) {
    if (CollectionUtils.isEmpty(ids)) {
      return List.of();
    }
    return getEntityManager()
        .createQuery("SELECT a.userId FROM AccountEntity a WHERE a.id IN :ids", UUID.class)
        .setParameter("ids", ids)
        .getResultList();
  }

  /** {@inheritDoc} */
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

  /** {@inheritDoc} */
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
}
