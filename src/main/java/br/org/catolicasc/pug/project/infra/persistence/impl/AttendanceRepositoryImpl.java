package br.org.catolicasc.pug.project.infra.persistence.impl;

import br.org.catolicasc.pug.project.domain.Attendance;
import br.org.catolicasc.pug.project.domain.AttendanceRepository;
import br.org.catolicasc.pug.project.domain.enums.AttendanceStatus;
import br.org.catolicasc.pug.project.infra.AttendanceMapper;
import br.org.catolicasc.pug.project.infra.persistence.AttendanceEntity;
import br.org.catolicasc.pug.shared.utils.StringUtils;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementation of the {@link AttendanceRepository} utilizing Hibernate ORM with Panache.
 *
 * <p>Bridges the pure domain repository interface with the persistence layer.
 */
@ApplicationScoped
public class AttendanceRepositoryImpl
    implements AttendanceRepository, PanacheRepositoryBase<AttendanceEntity, UUID> {

  /** {@inheritDoc} */
  @Transactional
  @Override
  public long deleteAllByEnrollmentId(UUID projectId, UUID formerStudentId) {
    if (projectId == null || formerStudentId == null) {
      return 0;
    }
    long deleted = delete("projectId = ?1 and formerStudentId = ?2", projectId, formerStudentId);
    flush();
    return deleted;
  }

  /** {@inheritDoc} */
  @Transactional
  @Override
  public long deleteAllWaitingValidationByProjectId(UUID projectId) {
    if (projectId == null) {
      return 0;
    }
    long deleted =
        delete("projectId = ?1 and status = ?2", projectId, AttendanceStatus.WAITING.name());
    flush();
    return deleted;
  }

  /** {@inheritDoc} */
  @Transactional
  @Override
  public boolean deleteById(UUID id) {
    if (id == null) {
      return false;
    }
    boolean deleted = delete("id", id) > 0;
    flush();
    return deleted;
  }

  /** {@inheritDoc} */
  @Override
  public boolean existsByQrHash(String qrHash) {
    if (StringUtils.isEmpty(qrHash)) {
      return false;
    }
    return count("qrValidationHash = ?1", qrHash) > 0;
  }

  /** {@inheritDoc} */
  @Override
  public boolean existsByValidatedBy(UUID accountId) {
    if (accountId == null) {
      return false;
    }
    return count("validatedBy", accountId) > 0;
  }

  /** {@inheritDoc} */
  @Override
  public Optional<Attendance> findOptionalById(UUID id) {
    if (id == null) {
      return Optional.empty();
    }
    return findByIdOptional(id).map(AttendanceMapper::toDomain);
  }

  /** {@inheritDoc} */
  @Override
  public Optional<Attendance> findOptionalByQrHash(String qrHash) {
    if (StringUtils.isEmpty(qrHash)) {
      return Optional.empty();
    }
    return find("qrValidationHash", qrHash).firstResultOptional().map(AttendanceMapper::toDomain);
  }

  /** {@inheritDoc} */
  @Transactional
  @Override
  public Attendance persist(Attendance entity) {
    if (entity == null) {
      return null;
    }
    var e = AttendanceMapper.toEntity(entity);
    persistAndFlush(e);
    return AttendanceMapper.toDomain(e);
  }

  /** {@inheritDoc} */
  @Transactional
  @Override
  public void update(Attendance entity) {
    if (entity == null || entity.getId() == null) {
      return;
    }
    AttendanceEntity managed = findById(entity.getId());
    if (managed != null) {
      AttendanceMapper.copy(entity, managed);
    }
  }
}
