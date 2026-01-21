package com.pug.identity.infra.persistence.impl;

import com.pug.identity.domain.Admin;
import com.pug.identity.domain.IAdminRepository;
import com.pug.identity.infra.AdminMapper;
import com.pug.identity.infra.persistence.AdminEntity;
import com.pug.shared.exceptions.AppValidationException;
import com.pug.shared.utils.CollectionUtils;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Panache implementation for Admins persistence.
 */
@ApplicationScoped
public class AdminRepository
        implements IAdminRepository, PanacheRepositoryBase<AdminEntity, UUID> {

  @Transactional
  @Override
  public Admin persist(Admin entity) throws AppValidationException {
    if (entity == null || entity.getAccountId() == null) {
      return null;
    }
    AdminEntity e = AdminMapper.toEntity(entity);
    persistAndFlush(e);
    return AdminMapper.toDomain(e);
  }

  @Transactional
  @Override
  public List<Admin> persistAll(Iterable<Admin> entities) throws AppValidationException {
    if (CollectionUtils.isEmpty(entities)) {
      return List.of();
    }
    var batch = new ArrayList<AdminEntity>();
    for (Admin d : entities) {
      if (d == null || d.getAccountId() == null) {
        continue;
      }
      batch.add(AdminMapper.toEntity(d));
    }
    if (batch.isEmpty()) {
      return List.of();
    }
    persist(batch);
    flush();
    return batch.stream().map(AdminMapper::toDomain).toList();
  }

  @Transactional
  @Override
  public long deleteByIds(Iterable<UUID> ids) {
    if (CollectionUtils.isEmpty(ids)) {
      return 0L;
    }
    long n = delete("accountId in ?1", ids);
    flush();
    getEntityManager().clear();
    return n;
  }

  @Override
  public Optional<Admin> findOptionalById(UUID accountId) throws AppValidationException {
    return find("accountId", accountId).firstResultOptional().map(AdminMapper::toDomain);
  }

  @Override
  public List<Admin> listAllAdmins() throws AppValidationException {
    return findAll().list().stream().map(AdminMapper::toDomain).toList();
  }

  @Override
  public boolean existsAnyByAccountIdIn(Iterable<UUID> accountIds) {
    if (CollectionUtils.isEmpty(accountIds)) {
      return false;
    }
    return find("accountId in ?1", accountIds).firstResultOptional().isPresent();
  }
}