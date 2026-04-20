package br.org.catolicasc.pug.partner.infra.persistence.impl;

import br.org.catolicasc.pug.partner.domain.Staff;
import br.org.catolicasc.pug.partner.domain.StaffRepository;
import br.org.catolicasc.pug.partner.infra.StaffMapper;
import br.org.catolicasc.pug.partner.infra.persistence.StaffEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementation of the {@link StaffRepository} utilizing Hibernate ORM with Panache.
 *
 * <p>This application-scoped bean handles the persistence and retrieval of Staff privileges,
 * linking authentication accounts to partner organizations.
 */
@ApplicationScoped
public class StaffRepositoryImpl
    implements StaffRepository, PanacheRepositoryBase<StaffEntity, UUID> {

  /** {@inheritDoc} */
  @Transactional
  @Override
  public boolean deleteByAccountId(UUID accountId) {
    if (accountId == null) {
      return false;
    }
    var deleted = delete("id", accountId) > 0;
    flush();
    return deleted;
  }

  /** {@inheritDoc} */
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

  /** {@inheritDoc} */
  @Override
  public boolean existsByAccountIdAndEntityId(UUID accountId, UUID entityId) {
    if (accountId == null || entityId == null) {
      return false;
    }
    return count("accountId = ?1 and entityId = ?2", accountId, entityId) > 0;
  }

  /** {@inheritDoc} */
  @Override
  public Optional<Staff> findOptionalByAccountId(UUID accountId) {
    return find("accountId = ?1", accountId).firstResultOptional().map(StaffMapper::toDomain);
  }

  /** {@inheritDoc} */
  @Override
  public List<Staff> listAllByEntityId(UUID entityId) {
    return find("entityId = ?1", entityId).list().stream().map(StaffMapper::toDomain).toList();
  }

  /** {@inheritDoc} */
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
}
