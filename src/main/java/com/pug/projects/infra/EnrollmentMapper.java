package com.pug.projects.infra;

import com.pug.academic.infra.read.dtos.StudentView;
import com.pug.projects.domain.Enrollment;
import com.pug.projects.domain.enums.EnrollmentStatus;
import com.pug.projects.domain.vos.EnrollmentIdentifier;
import com.pug.projects.domain.vos.EnrollmentInfo;
import com.pug.projects.infra.persistence.EnrollmentEntity;
import com.pug.projects.infra.read.dtos.EnrollmentView;
import com.pug.projects.infra.read.dtos.ProjectView;
import com.pug.shared.domain.vos.AuditInfo;

/**
 * Stateless utility class responsible for mapping between Enrollment boundary layers.
 *
 * <p>This mapper acts as an anti-corruption layer, handling the conversion of the composite
 * database keys and timeline metadata between the Domain and Persistence layers.
 */
public final class EnrollmentMapper {

  /** Private constructor to prevent instantiation. */
  private EnrollmentMapper() {}

  /**
   * Reconstitutes a pure Domain {@link Enrollment} aggregate from a JPA {@link EnrollmentEntity}.
   *
   * @param e the JPA persistence entity to convert
   * @return a fully constructed Domain {@link Enrollment}, or {@code null} if the input entity is
   *     null
   */
  public static Enrollment toDomain(EnrollmentEntity e) {
    if (e == null) {
      return null;
    }

    EnrollmentIdentifier identifier =
        EnrollmentIdentifier.builder()
            .projectId(e.getId().getProjectId())
            .studentId(e.getId().getStudentId())
            .build();

    EnrollmentInfo info =
        EnrollmentInfo.builder()
            .acceptedAt(e.getAcceptedAt())
            .closingStatusAt(e.getClosingStatusAt())
            .auditInfo(AuditInfo.factory(e.getCreatedAt(), e.getUpdatedAt()))
            .build();

    return Enrollment.builder()
        .identifier(identifier)
        .status(EnrollmentStatus.valueOf(e.getStatus()))
        .enrollmentInfo(info)
        .build();
  }

  /**
   * Translates a pure Domain {@link Enrollment} aggregate into a newly instantiated JPA {@link
   * EnrollmentEntity}.
   *
   * @param d the Domain aggregate to convert
   * @return a newly constructed JPA {@link EnrollmentEntity}, or {@code null} if the input domain
   *     is null
   */
  public static EnrollmentEntity toEntity(Enrollment d) {
    if (d == null) {
      return null;
    }

    EnrollmentEntity.EnrollmentsId eid =
        new EnrollmentEntity.EnrollmentsId(
            d.getIdentifier().getProjectId(), d.getIdentifier().getStudentId());

    return EnrollmentEntity.builder()
        .id(eid)
        .status(d.getStatus().name())
        .acceptedAt(d.getEnrollmentInfo().getAcceptedAt())
        .closingStatusAt(d.getEnrollmentInfo().getClosingStatusAt())
        .createdAt(d.getEnrollmentInfo().getAuditInfo().getCreatedAt())
        .updatedAt(d.getEnrollmentInfo().getAuditInfo().getUpdatedAt())
        .build();
  }

  /**
   * Updates an existing, attached JPA {@link EnrollmentEntity} with the current state of a Domain
   * {@link Enrollment}.
   *
   * @param d the Domain aggregate containing the updated state
   * @param e the existing, attached JPA entity to update in-place
   */
  public static void copy(Enrollment d, EnrollmentEntity e) {
    if (d == null || e == null) {
      return;
    }
    e.setStatus(d.getStatus().name());
    e.setAcceptedAt(d.getEnrollmentInfo().getAcceptedAt());
    e.setClosingStatusAt(d.getEnrollmentInfo().getClosingStatusAt());
  }

  /**
   * Projects a raw JPA {@link EnrollmentEntity} and its pre-resolved nested views into a
   * comprehensive {@link EnrollmentView} DTO.
   *
   * <p>Since resolving Projects and Students requires deep query joins across multiple modules,
   * this mapper expects the query layer to provide the resolved nested views.
   *
   * @param e the JPA entity representing the enrollment lifecycle
   * @param project the pre-resolved, fully populated view of the project
   * @param student the pre-resolved, fully populated view of the student
   * @return a fully populated {@link EnrollmentView} DTO
   */
  public static EnrollmentView toView(
      EnrollmentEntity e, ProjectView project, StudentView student) {
    if (e == null) return null;

    return new EnrollmentView(
        project,
        student,
        EnrollmentStatus.valueOf(e.getStatus()),
        e.getCreatedAt(),
        e.getUpdatedAt(),
        e.getAcceptedAt(),
        e.getClosingStatusAt());
  }
}
