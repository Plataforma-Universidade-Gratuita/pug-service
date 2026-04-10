package com.pug.project.infra;

import com.pug.project.domain.Attendance;
import com.pug.project.domain.enums.AttendanceStatus;
import com.pug.project.domain.vos.AttendanceInfo;
import com.pug.project.domain.vos.EnrollmentIdentifier;
import com.pug.project.domain.vos.QrValidationInfo;
import com.pug.project.infra.persistence.AttendanceEntity;
import com.pug.project.infra.read.dtos.AttendanceView;
import com.pug.shared.domain.vos.AuditInfo;

/**
 * Stateless utility class responsible for mapping between Attendance boundary layers.
 *
 * <p>This mapper acts as an anti-corruption layer, handling the conversion of Value Objects and
 * database entities between the Domain and Persistence/Read layers.
 */
public final class AttendanceMapper {

  /** Private constructor to prevent instantiation. */
  private AttendanceMapper() {}

  /**
   * Reconstitutes a pure Domain {@link Attendance} aggregate from a JPA {@link AttendanceEntity}.
   *
   * @param e the JPA persistence entity to convert
   * @return a fully constructed Domain {@link Attendance}, or {@code null} if the input entity is
   *     null
   */
  public static Attendance toDomain(AttendanceEntity e) {
    if (e == null) {
      return null;
    }

    EnrollmentIdentifier identifier =
        EnrollmentIdentifier.builder()
            .projectId(e.getProjectId())
            .studentId(e.getStudentId())
            .build();

    QrValidationInfo qrInfo = QrValidationInfo.factory(e.getDuration(), e.getQrValidationHash());

    AttendanceInfo attInfo =
        AttendanceInfo.builder()
            .validatedBy(e.getValidatedBy())
            .validatedAt(e.getValidatedAt())
            .auditInfo(AuditInfo.factory(e.getCreatedAt(), e.getUpdatedAt()))
            .build();

    return Attendance.builder()
        .id(e.getId())
        .enrollmentIdentifier(identifier)
        .qrValidationInfo(qrInfo)
        .attendanceInfo(attInfo)
        .status(AttendanceStatus.valueOf(e.getStatus()))
        .build();
  }

  /**
   * Translates a pure Domain {@link Attendance} aggregate into a newly instantiated JPA {@link
   * AttendanceEntity}.
   *
   * @param d the Domain aggregate to convert
   * @return a newly constructed JPA {@link AttendanceEntity}, or {@code null} if the input domain
   *     is null
   */
  public static AttendanceEntity toEntity(Attendance d) {
    if (d == null) {
      return null;
    }

    return AttendanceEntity.builder()
        .id(d.getId())
        .projectId(d.getEnrollmentIdentifier().getProjectId())
        .studentId(d.getEnrollmentIdentifier().getStudentId())
        .duration(d.getQrValidationInfo().getDuration())
        .qrValidationHash(d.getQrValidationInfo().getQrValidationHash())
        .status(d.getStatus().name())
        .validatedBy(d.getAttendanceInfo().getValidatedBy())
        .validatedAt(d.getAttendanceInfo().getValidatedAt())
        .createdAt(d.getAttendanceInfo().getAuditInfo().getCreatedAt())
        .updatedAt(d.getAttendanceInfo().getAuditInfo().getUpdatedAt())
        .build();
  }

  /**
   * Updates an existing, attached JPA {@link AttendanceEntity} with the current state of a Domain
   * {@link Attendance}.
   *
   * @param d the Domain aggregate containing the updated state
   * @param e the existing, attached JPA entity to update in-place
   */
  public static void copy(Attendance d, AttendanceEntity e) {
    if (d == null || e == null) {
      return;
    }
    e.setDuration(d.getQrValidationInfo().getDuration());
    e.setQrValidationHash(d.getQrValidationInfo().getQrValidationHash());
    e.setStatus(d.getStatus().name());
    e.setValidatedBy(d.getAttendanceInfo().getValidatedBy());
    e.setValidatedAt(d.getAttendanceInfo().getValidatedAt());
    e.setUpdatedAt(d.getAttendanceInfo().getAuditInfo().getUpdatedAt());
  }

  /**
   * Projects a raw JPA {@link AttendanceEntity} into a lightweight {@link AttendanceView} DTO.
   *
   * <p>This mapping flattens the persistence entity into a CQRS read model, exposing only the
   * necessary identifiers (e.g., {@code projectId}, {@code studentId}, {@code validatedById})
   * rather than nesting complex objects.
   *
   * @param e the JPA entity representing the raw attendance record
   * @return a fully populated {@link AttendanceView} DTO
   */
  public static AttendanceView toView(AttendanceEntity e) {
    if (e == null) {
      return null;
    }

    return new AttendanceView(
        e.getId(),
        e.getProjectId(),
        e.getStudentId(),
        e.getDuration(),
        e.getQrValidationHash(),
        AttendanceStatus.valueOf(e.getStatus()),
        e.getValidatedBy(),
        e.getValidatedAt(),
        e.getCreatedAt(),
        e.getUpdatedAt());
  }
}
