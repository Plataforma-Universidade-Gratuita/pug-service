package com.pug.identity.infra.persistence;

import com.pug.identity.domain.Admin;
import com.pug.identity.domain.AdminsRepository;
import com.pug.identity.infra.AdminMapper;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementation of the AdminsRepository using PanacheRepositoryBase for CRUD operations on
 * AdminsEntity.
 */
@ApplicationScoped
public class AdminsRepositoryImpl
    implements AdminsRepository, PanacheRepositoryBase<AdminsEntity, UUID> {

  @Transactional
  @Override
  public Admin persist(Admin admin) {
    if (admin == null || admin.getUser() == null || admin.getUser().getId() == null) {
      return null;
    }

    AdminsEntity e = AdminMapper.toEntity(admin);
    UsersEntity ref = getEntityManager().getReference(UsersEntity.class, admin.getUser().getId());
    e.setUser(ref);
    persistAndFlush(e);

    AdminsEntity loaded =
        find("select a from AdminsEntity a join fetch a.user where a.userId = ?1", e.getUserId())
            .firstResultOptional()
            .orElse(e);
    return AdminMapper.toDomain(loaded);
  }

  @Transactional
  @Override
  public List<Admin> persistAll(Iterable<Admin> admins) {
    if (admins == null) {
      return List.of();
    }
    var batch = new ArrayList<AdminsEntity>();
    for (Admin d : admins) {
      if (d == null || d.getUser() == null || d.getUser().getId() == null) {
        continue;
      }
      var e = AdminMapper.toEntity(d);
      var ref = getEntityManager().getReference(UsersEntity.class, d.getUser().getId());
      e.setUser(ref);
      batch.add(e);
    }
    if (batch.isEmpty()) {
      return List.of();
    }

    persist(batch);
    flush();

    var ids = batch.stream().map(AdminsEntity::getUserId).toList();
    List<AdminsEntity> loaded =
        find("select a from AdminsEntity a join fetch a.user where a.userId in ?1", ids).list();
    if (loaded.size() == batch.size()) {
      return loaded.stream().map(AdminMapper::toDomain).toList();
    }
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
    return find("select a from AdminsEntity a join fetch a.user where a.userId = ?1", userId)
        .firstResultOptional()
        .map(AdminMapper::toDomain);
  }

  @Override
  public List<Admin> listAllAdmins() {
    return find("select a from AdminsEntity a join fetch a.user").list().stream()
        .map(AdminMapper::toDomain)
        .toList();
  }

  @Override
  public boolean existsByUserId(UUID userId) {
    return find("userId", userId).firstResultOptional().isPresent();
  }
}
