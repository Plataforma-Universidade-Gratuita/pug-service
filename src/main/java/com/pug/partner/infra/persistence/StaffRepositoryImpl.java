package com.pug.partner.infra.persistence;

import com.pug.partner.domain.Staff;
import com.pug.partner.domain.StaffRepository;
import com.pug.partner.infra.StaffMapper;
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
 * Implementation of the StaffRepository using Panache.
 */
@ApplicationScoped
public class StaffRepositoryImpl
        implements StaffRepository, PanacheRepositoryBase<StaffEntity, UUID> {

  @Transactional
  @Override
  public Staff persist(Staff entity) throws AppValidationException {
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
  public List<Staff> persistAll(Iterable<Staff> entities) throws AppValidationException {
    if (CollectionUtils.isEmpty(entities)) {
      return List.of();
    }

    var batch = new ArrayList<StaffEntity>();
    for (var s : entities) {
      if (s != null) {
        batch.add(StaffMapper.toEntity(s));
      }
    }
    if (batch.isEmpty()) {
      return List.of();
    }

    persist(batch);
    flush();

    var accountIds = batch.stream().map(StaffEntity::getAccountId).toList();
    List<StaffEntity> loaded = find("accountId in ?1", accountIds).list();

    return (loaded.size() == batch.size() ? loaded : batch)
            .stream().map(StaffMapper::toDomain).toList();
  }

  @Transactional
  @Override
  public void update(Staff entity) {
    if (entity == null) {
      return;
    }
    StaffEntity managed = getEntityManager().find(StaffEntity.class, entity.getAccountId());
    if (managed == null) {
      return;
    }
    StaffMapper.copy(entity, managed);
  }

  @Transactional
  @Override
  public long deleteByIds(Iterable<UUID> accountIds) {
    if (CollectionUtils.isEmpty(accountIds)) {
      return 0L;
    }
    long n = delete("accountId in ?1", accountIds);
    flush();
    getEntityManager().clear();
    return n;
  }

  @Override
  public Optional<Staff> findOptionalById(UUID accountId) throws AppValidationException {
    return find("accountId = ?1", accountId).firstResultOptional().map(StaffMapper::toDomain);
  }

  @Override
  public List<Staff> listAllStaff() throws AppValidationException {
    return listAll().stream().map(StaffMapper::toDomain).toList();
  }

  @Override
  public List<Staff> listAllByEntityId(UUID entityId) throws AppValidationException {
    return find("entityId = ?1", entityId).list().stream().map(StaffMapper::toDomain).toList();
  }

  @Override
  public boolean existsByAccountId(UUID accountId) {
    return find("accountId", accountId).firstResultOptional().isPresent();
  }

  @Override
  public boolean existsAnyByAccountIdIn(Iterable<UUID> accountIds) {
    if (CollectionUtils.isEmpty(accountIds)) {
      return false;
    }
    return find("accountId in ?1", accountIds).firstResultOptional().isPresent();
  }
}