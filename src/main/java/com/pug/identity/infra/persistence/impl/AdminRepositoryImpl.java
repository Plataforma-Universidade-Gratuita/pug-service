package com.pug.identity.infra.persistence.impl;

import com.pug.identity.domain.Admin;
import com.pug.identity.domain.AdminRepository;
import com.pug.identity.infra.AdminMapper;
import com.pug.identity.infra.persistence.AdminEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.util.Optional;
import java.util.UUID;

/**
 * Implementation of the {@link AdminRepository} utilizing Hibernate ORM with Panache.
 * <p>
 * This application-scoped bean bridges the pure domain repository interface with
 * the underlying database infrastructure. It handles the persistence and retrieval
 * of administrative privileges strictly tied to user accounts.
 */
@ApplicationScoped
public class AdminRepositoryImpl
        implements AdminRepository, PanacheRepositoryBase<AdminEntity, UUID> {

  /**
   * {@inheritDoc}
   */
  @Transactional
  @Override
  public Admin persist(Admin entity) {
    if (entity == null || entity.getAccountId() == null) {
      return null;
    }
    AdminEntity e = AdminMapper.toEntity(entity);
    persistAndFlush(e);
    return AdminMapper.toDomain(e);
  }

  /**
   * {@inheritDoc}
   */
  @Transactional
  @Override
  public void update(Admin entity) {
    if (entity == null || entity.getAccountId() == null) {
      return;
    }
    AdminEntity e = findById(entity.getAccountId());
    if (e == null) {
      return;
    }
    AdminMapper.copy(entity, e);
  }

  /**
   * {@inheritDoc}
   */
  @Transactional
  @Override
  public boolean deleteByAccountId(UUID accountId) {
    if (accountId == null) {
      return false;
    }
    var deleted = PanacheRepositoryBase.super.deleteById(accountId);
    flush();
    return deleted;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Optional<Admin> findOptionalByAccountId(UUID accountId) {
    return find("accountId", accountId).firstResultOptional().map(AdminMapper::toDomain);
  }
}