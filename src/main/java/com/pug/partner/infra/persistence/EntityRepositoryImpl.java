package com.pug.partner.infra.persistence;

import com.pug.partner.domain.Entity;
import com.pug.partner.domain.EntityRepository;
import com.pug.partner.infra.EntityMapper;
import com.pug.shared.exceptions.AppValidationException;
import com.pug.shared.utils.CollectionUtils;
import com.pug.shared.utils.StringUtils;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementation of the EntityRepository using Panache.
 */
@ApplicationScoped
public class EntityRepositoryImpl
        implements EntityRepository, PanacheRepositoryBase<EntityEntity, UUID> {

  @Transactional
  @Override
  public Entity persist(Entity entity) throws AppValidationException { // Added throws
    if (entity == null) {
      return null;
    }
    EntityEntity e = EntityMapper.toEntity(entity);
    persistAndFlush(e);
    EntityEntity loaded =
            find("select e from EntityEntity e where e.id = ?1", e.getId())
                    .firstResultOptional()
                    .orElse(e);
    return EntityMapper.toDomain(loaded);
  }

  @Transactional
  @Override
  public void update(Entity entity) {
    if (entity == null) {
      return;
    }
    EntityEntity managed = getEntityManager().find(EntityEntity.class, entity.getId());
    if (managed == null) {
      return;
    }
    EntityMapper.copy(entity, managed);
  }

  @Transactional
  @Override
  public long deleteByIds(Iterable<UUID> ids) {
    if (CollectionUtils.isEmpty(ids)) {
      return 0L;
    }
    long n = delete("id in ?1", ids);
    flush();
    getEntityManager().clear();
    return n;
  }

  @Override
  public Optional<Entity> findOptionalById(UUID id) throws AppValidationException {
    return find("select e from EntityEntity e where e.id = ?1", id)
            .firstResultOptional()
            .map(EntityMapper::toDomain);
  }

  @Override
  public Optional<Entity> findOptionalByCnpj(String cnpj) throws AppValidationException {
    return find("select e from EntityEntity e where e.cnpj = ?1", cnpj)
            .firstResultOptional()
            .map(EntityMapper::toDomain);
  }

  @Override
  public List<Entity> listAllEntities() throws AppValidationException {
    return listAll().stream().map(EntityMapper::toDomain).toList();
  }

  @Override
  public boolean existsByCnpj(String cnpj) {
    if (StringUtils.isEmpty(cnpj)) {
      return false;
    }
    return find("cnpj", cnpj).firstResultOptional().isPresent();
  }

  @Override
  public boolean existsAnyByCityIdIn(Iterable<UUID> cityIds) {
    if (CollectionUtils.isEmpty(cityIds)) {
      return false;
    }
    return find("cityId in ?1", cityIds).firstResultOptional().isPresent();
  }
}