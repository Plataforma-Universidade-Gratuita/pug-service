package com.pug.project.infra.persistence.impl;

import com.pug.project.domain.Attendance;
import com.pug.project.domain.AttendanceRepository;
import com.pug.project.infra.AttendanceMapper;
import com.pug.project.infra.persistence.AttendanceEntity;
import com.pug.shared.utils.StringUtils;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.util.Optional;
import java.util.UUID;

/** Implementation of the {@link AttendanceRepository} utilizing Hibernate ORM with Panache. */
@ApplicationScoped
public class AttendanceRepositoryImpl
    implements AttendanceRepository, PanacheRepositoryBase<AttendanceEntity, UUID> {

  @Transactional
  @Override
  public Attendance persist(Attendance entity) {
    if (entity == null) return null;
    var e = AttendanceMapper.toEntity(entity);
    persistAndFlush(e);
    return AttendanceMapper.toDomain(e);
  }

  @Transactional
  @Override
  public void update(Attendance entity) {
    if (entity == null || entity.getId() == null) return;
    AttendanceEntity managed = findById(entity.getId());
    if (managed != null) {
      AttendanceMapper.copy(entity, managed);
    }
  }

  @Transactional
  @Override
  public boolean deleteById(UUID id) {
    if (id == null) return false;
    var deleted = delete("id", id) > 0;
    flush();
    return deleted;
  }

  @Override
  public Optional<Attendance> findOptionalById(UUID id) {
    return findByIdOptional(id).map(AttendanceMapper::toDomain);
  }

  @Override
  public boolean existsByQrHash(String qrHash) {
    if (StringUtils.isEmpty(qrHash)) return false;
    return count("qrValidationHash = ?1", qrHash) > 0;
  }

  @Override
  public boolean existsByValidatedBy(UUID staffAccountId) {
    return false;
  }
}
