package com.pug.identity.infra.persistence;

import com.pug.identity.domain.Admin;
import com.pug.identity.domain.AdminRepository;
import com.pug.identity.infra.AdminMapper;
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
  public Admin persist(Admin admin) {
    if (admin == null || admin.getUserId() == null) {
      return null;
    }
    AdminEntity e = AdminMapper.toEntity(admin);
    persistAndFlush(e);
    return AdminMapper.toDomain(e);
  }

  @Transactional
  @Override
  public List<Admin> persistAll(Iterable<Admin> admins) {
    if (admins == null) {
      return List.of();
    }
    var batch = new ArrayList<AdminEntity>();
    for (Admin d : admins) {
      if (d == null || d.getUserId() == null) {
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
    if (ids == null || !ids.iterator().hasNext()) {
      return 0L;
    }
    long n = delete("userId in ?1", ids);
    flush();
    getEntityManager().clear();
    return n;
  }

  @Override
  public Optional<Admin> findOptionalById(UUID userId) {
    return find("userId", userId).firstResultOptional().map(AdminMapper::toDomain);
  }

  @Override
  public List<Admin> listAllAdmins() {
    return findAll().list().stream().map(AdminMapper::toDomain).toList();
  }

  @Override
  public boolean existsAnyByUserIdIn(Iterable<UUID> userIds) {
    if (userIds == null || !userIds.iterator().hasNext()) {
      return false;
    }
    return find("userId in ?1", userIds).firstResultOptional().isPresent();
  }
}
