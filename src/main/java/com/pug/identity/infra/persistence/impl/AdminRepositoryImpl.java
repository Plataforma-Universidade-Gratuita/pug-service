package com.pug.identity.infra.persistence.impl;

import com.pug.identity.domain.Admin;
import com.pug.identity.domain.AdminRepository;
import com.pug.identity.infra.AdminMapper;
import com.pug.identity.infra.persistence.AdminEntity;
import com.pug.shared.utils.CollectionUtils;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** Panache implementation for Admins persistence. */
@ApplicationScoped
public class AdminRepositoryImpl
    implements AdminRepository, PanacheRepositoryBase<AdminEntity, UUID> {

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

  @Override
  public Optional<Admin> findOptionalByAccountId(UUID accountId) {
    return find("accountId", accountId).firstResultOptional().map(AdminMapper::toDomain);
  }

  @Override
  public List<Admin> listAllAdmins() {
    return findAll().list().stream().map(AdminMapper::toDomain).toList();
  }
}
