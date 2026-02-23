package com.pug.partner.infra.persistence.impl;

import com.pug.partner.domain.Staff;
import com.pug.partner.domain.StaffRepository;
import com.pug.partner.infra.StaffMapper;
import com.pug.partner.infra.persistence.StaffEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** Implementation of the StaffRepository using Panache. */
@ApplicationScoped
public class StaffRepositoryImpl
    implements StaffRepository, PanacheRepositoryBase<StaffEntity, UUID> {

  @Transactional
  @Override
  public Staff persist(Staff entity) {
    if (entity == null) {
      return null;
    }
    StaffEntity e = StaffMapper.toEntity(entity);
    persistAndFlush(e);
    StaffEntity loaded = find("accountId = ?1", e.getAccountId()).firstResultOptional().orElse(e);
    return StaffMapper.toDomain(loaded);
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

  @Transactional
  @Override
  public long deleteByEntityId(UUID entityId) {
    if (entityId == null) {
      return 0;
    }
    long deletedCount = delete("entityId", entityId);
    flush();
    return deletedCount;
  }

  @Override
  public Optional<Staff> findOptionalByAccountId(UUID accountId) {
    return find("accountId = ?1", accountId).firstResultOptional().map(StaffMapper::toDomain);
  }

  @Override
  public List<Staff> listAllStaff() {
    return listAll().stream().map(StaffMapper::toDomain).toList();
  }

  @Override
  public List<Staff> listAllByEntityId(UUID entityId) {
    return find("entityId = ?1", entityId).list().stream().map(StaffMapper::toDomain).toList();
  }

  @Override
  public boolean existsByAccountId(UUID accountId) {
    return find("accountId", accountId).firstResultOptional().isPresent();
  }
}
