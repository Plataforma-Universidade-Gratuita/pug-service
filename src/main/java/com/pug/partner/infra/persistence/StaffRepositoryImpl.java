package com.pug.partner.infra.persistence;

import com.pug.partner.domain.Staff;
import com.pug.partner.domain.StaffRepository;
import com.pug.partner.infra.StaffMapper;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** Implementation of the StaffRepository using Panache. */
@ApplicationScoped
public class StaffRepositoryImpl
    implements StaffRepository, PanacheRepositoryBase<StaffEntity, UUID> {

  @Transactional
  @Override
  public Staff persist(Staff staff) {
    if (staff == null) {
      return null;
    }
    StaffEntity e = StaffMapper.toEntity(staff);
    persistAndFlush(e);
    StaffEntity loaded = find("userId = ?1", e.getUserId()).firstResultOptional().orElse(e);
    return StaffMapper.toDomain(loaded);
  }

  @Transactional
  @Override
  public List<Staff> persistAll(Iterable<Staff> staff) {
    if (staff == null || !staff.iterator().hasNext()) {
      return List.of();
    }

    var batch = new ArrayList<StaffEntity>();
    for (var s : staff) {
      if (s != null) {
        batch.add(StaffMapper.toEntity(s));
      }
    }
    if (batch.isEmpty()) {
      return List.of();
    }

    persist(batch);
    flush();

    var userIds = batch.stream().map(StaffEntity::getUserId).toList();
    List<StaffEntity> loaded = find("userId in ?1", userIds).list();

    return (loaded.size() == batch.size() ? loaded : batch)
        .stream().map(StaffMapper::toDomain).toList();
  }

  @Transactional
  @Override
  public long deleteByUserIds(Iterable<UUID> userIds) {
    if (userIds == null || !userIds.iterator().hasNext()) {
      return 0L;
    }
    long n = delete("userId in ?1", userIds);
    flush();
    getEntityManager().clear();
    return n;
  }

  @Override
  public Optional<Staff> findOptionalByUserId(UUID userId) {
    return find("userId = ?1", userId).firstResultOptional().map(StaffMapper::toDomain);
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
  public boolean existsByUserId(UUID userId) {
    return find("userId", userId).firstResultOptional().isPresent();
  }

  @Override
  public boolean existsAnyByAccountIdIn(Iterable<UUID> accountIds) {
    if (accountIds == null || !accountIds.iterator().hasNext()) {
      return false;
    }
    return find("userId in ?1", accountIds).firstResultOptional().isPresent();
  }
}
