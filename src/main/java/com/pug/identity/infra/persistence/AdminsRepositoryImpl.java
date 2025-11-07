package com.pug.identity.infra.persistence;

import com.pug.identity.domain.AdminsRepository;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
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
  public void persist(AdminsEntity entity) {
    persistAndFlush(entity);
  }

  @Transactional
  @Override
  public void persistAll(Iterable<AdminsEntity> entities) {
    persist(entities);
    flush();
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
  public Optional<AdminsEntity> findOptionalById(UUID userId) {
    return find("select a from AdminsEntity a join fetch a.user where a.userId = ?1", userId)
        .firstResultOptional();
  }

  @Override
  public List<AdminsEntity> listAllAdmins() {
    return find("select a from AdminsEntity a join fetch a.user").list();
  }

  @Override
  public boolean existsByUserId(UUID userId) {
    return find("userId", userId).firstResultOptional().isPresent();
  }
}
