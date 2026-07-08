package br.org.catolicasc.pug.project.infra;

import br.org.catolicasc.pug.project.domain.Enrollment;
import br.org.catolicasc.pug.project.domain.enums.EnrollmentStatus;
import br.org.catolicasc.pug.project.domain.vos.EnrollmentIdentifier;
import br.org.catolicasc.pug.project.domain.vos.EnrollmentInfo;
import br.org.catolicasc.pug.project.infra.persistence.EnrollmentEntity;
import br.org.catolicasc.pug.shared.domain.vos.AuditInfo;

/**
 * Stateless utility class responsible for mapping between Enrollment boundary layers.
 *
 * <p>This mapper acts as an anti-corruption layer, handling the conversion of the composite
 * database keys and timeline metadata between the Domain and Persistence layers.
 */
public final class EnrollmentMapper {

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
            .formerStudentId(e.getId().getFormerStudentId())
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
            d.getIdentifier().getProjectId(), d.getIdentifier().getFormerStudentId());

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
    e.setCreatedAt(d.getEnrollmentInfo().getAuditInfo().getCreatedAt());
    e.setUpdatedAt(d.getEnrollmentInfo().getAuditInfo().getUpdatedAt());
  }
}
