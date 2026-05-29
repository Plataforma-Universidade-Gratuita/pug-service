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

/** Implementation of the {@link StaffRepository} utilizing Hibernate ORM with Panache. */
@ApplicationScoped
public class StaffRepositoryImpl
    implements StaffRepository, PanacheRepositoryBase<StaffEntity, UUID> {

  @Transactional
  @Override
  public boolean deleteByAccountId(UUID accountId) {
    if (accountId == null) {
      return false;
    }
    boolean deleted = delete("accountId", accountId) > 0;
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
  public boolean existsByAccountIdAndEntityId(UUID accountId, UUID entityId) {
    if (accountId == null || entityId == null) {
      return false;
    }
    return count("accountId = ?1 and entityId = ?2", accountId, entityId) > 0;
  }

  @Override
  public boolean existsAnotherByEntityIdAndEmail(
      UUID entityId, String email, UUID excludedAccountId) {
    if (entityId == null || email == null) {
      return false;
    }

    Long count =
        getEntityManager()
            .createQuery(
                "select count(s.accountId) from StaffEntity s"
                    + " join AccountEntity a on a.id = s.accountId"
                    + " where s.entityId = :entityId and a.email = :email"
                    + " and (:excludedAccountId is null or s.accountId <> :excludedAccountId)",
                Long.class)
            .setParameter("entityId", entityId)
            .setParameter("email", email)
            .setParameter("excludedAccountId", excludedAccountId)
            .getSingleResult();

    return count != null && count > 0;
  }

  @Override
  public Optional<Staff> findOptionalByAccountId(UUID accountId) {
    return find("accountId = ?1", accountId).firstResultOptional().map(StaffMapper::toDomain);
  }

  @Override
  public List<Staff> listAllByEntityId(UUID entityId) {
    return find("entityId = ?1", entityId).list().stream().map(StaffMapper::toDomain).toList();
  }

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
  public void update(Staff entity) {
    if (entity == null || entity.getAccountId() == null) {
      return;
    }
    StaffEntity managed = findById(entity.getAccountId());
    if (managed == null) {
      return;
    }
    StaffMapper.copy(entity, managed);
  }
}
