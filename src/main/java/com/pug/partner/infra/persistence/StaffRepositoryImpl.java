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

  /**
   * Persists a Staff entity and returns the persisted domain object.
   *
   * @param staff the Staff domain object to persist.
   * @return the persisted Staff domain object.
   */
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

  /**
   * Persists multiple Staff entities and returns a list of the persisted domain objects.
   *
   * @param staff an iterable collection of Staff domain objects to persist.
   * @return a list of the persisted Staff domain objects.
   */
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

  /**
   * Deletes Staff entities by their user IDs.
   *
   * @param userIds an iterable collection of UUIDs representing the user IDs of the Staff entities
   *     to delete.
   * @return the number of Staff entities deleted.
   */
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

  /**
   * Finds a Staff entity by its user ID.
   *
   * @param userId the UUID of the user ID to find.
   * @return an Optional containing the found Staff domain object, or empty if not found.
   */
  @Override
  public Optional<Staff> findOptionalByUserId(UUID userId) {
    return find("userId = ?1", userId).firstResultOptional().map(StaffMapper::toDomain);
  }

  /**
   * Lists all Staff entities.
   *
   * @return a list of all Staff domain objects.
   */
  @Override
  public List<Staff> listAllStaff() {
    return listAll().stream().map(StaffMapper::toDomain).toList();
  }

  /**
   * Lists all Staff entities by their associated entity ID.
   *
   * @param entityId the UUID of
   * @return a list of Staff domain objects associated with the specified entity ID.
   */
  @Override
  public List<Staff> listAllByEntityId(UUID entityId) {
    return find("entityId = ?1", entityId).list().stream().map(StaffMapper::toDomain).toList();
  }

  /**
   * Checks if a Staff entity exists by its user ID.
   *
   * @param userId the UUID of the user ID to check.
   * @return true if a Staff entity with the specified user ID exists, false otherwise.
   */
  @Override
  public boolean existsByUserId(UUID userId) {
    return find("userId", userId).firstResultOptional().isPresent();
  }
}
