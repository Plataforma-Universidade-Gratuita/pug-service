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
  public void persist(Staff staff) {
    if (staff == null) {
      return;
    }
    persistAndFlush(StaffMapper.toEntity(staff));
  }

  @Transactional
  @Override
  public void persistAll(Iterable<Staff> staff) {
    if (staff == null || !staff.iterator().hasNext()) {
      return;
    }
    var batch = new ArrayList<StaffEntity>();
    for (var s : staff) {
      if (s != null) {
        batch.add(StaffMapper.toEntity(s));
      }
    }
    if (batch.isEmpty()) {
      return;
    }
    persist(batch);
    flush();
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
    return find(
            "select s from StaffEntity s "
                + "left join fetch s.user "
                + "left join fetch s.entity "
                + "where s.userId = ?1",
            userId)
        .firstResultOptional()
        .map(StaffMapper::toDomain);
  }

  @Override
  public List<Staff> listAllStaff() {
    return find("select s from StaffEntity s left join fetch s.user left join fetch s.entity")
        .list()
        .stream()
        .map(StaffMapper::toDomain)
        .toList();
  }

  @Override
  public List<Staff> listAllByEntityId(UUID entityId) {
    return find(
            "select s from StaffEntity s "
                + "left join fetch s.user "
                + "left join fetch s.entity "
                + "where s.entityId = ?1",
            entityId)
        .list()
        .stream()
        .map(StaffMapper::toDomain)
        .toList();
  }

  @Override
  public boolean existsByUserId(UUID userId) {
    return find("userId", userId).firstResultOptional().isPresent();
  }
}
