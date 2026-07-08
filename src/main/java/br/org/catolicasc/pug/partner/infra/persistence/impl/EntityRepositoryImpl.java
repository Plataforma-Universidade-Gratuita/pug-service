package br.org.catolicasc.pug.partner.infra.persistence.impl;

import br.org.catolicasc.pug.partner.domain.Entity;
import br.org.catolicasc.pug.partner.domain.EntityRepository;
import br.org.catolicasc.pug.partner.infra.EntityMapper;
import br.org.catolicasc.pug.partner.infra.persistence.EntityEntity;
import br.org.catolicasc.pug.shared.utils.StringUtils;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementation of the {@link EntityRepository} utilizing Hibernate ORM with Panache.
 *
 * <p>This application-scoped bean bridges the pure domain repository interface with the underlying
 * database infrastructure. It manages transaction boundaries, entity state transitions, and the
 * mapping between domain aggregates and JPA persistence entities.
 */
@ApplicationScoped
public class EntityRepositoryImpl
    implements EntityRepository, PanacheRepositoryBase<EntityEntity, UUID> {

  /** {@inheritDoc} */
  @Override
  @Transactional
  public boolean deleteById(UUID id) {
    if (id == null) {
      return false;
    }
    var deleted = delete("id", id) > 0;
    flush();
    return deleted;
  }

  /** {@inheritDoc} */
  @Override
  public boolean existsByCityId(UUID cityId) {
    if (cityId == null) {
      return false;
    }
    return count("cityId", cityId) > 0;
  }

  /** {@inheritDoc} */
  @Override
  public boolean existsByCnpj(String cnpj) {
    if (StringUtils.isEmpty(cnpj)) {
      return false;
    }
    return find("cnpj", cnpj).firstResultOptional().isPresent();
  }

  /** {@inheritDoc} */
  @Override
  public Optional<Entity> findOptionalById(UUID id) {
    return find("select e from EntityEntity e where e.id = ?1", id)
        .firstResultOptional()
        .map(EntityMapper::toDomain);
  }

  /** {@inheritDoc} */
  @Override
  @Transactional
  public Entity persist(Entity entity) {
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

  /** {@inheritDoc} */
  @Override
  @Transactional
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
}
