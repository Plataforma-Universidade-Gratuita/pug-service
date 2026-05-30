package br.org.catolicasc.pug.project.presenter.dtos.enrollments;

import br.org.catolicasc.pug.project.domain.enums.EnrollmentStatus;
import br.org.catolicasc.pug.shared.validation.UuidV7;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Request payload used by the paginated enrollment complex-search endpoint.
 *
 * @param projectIds optional project identifiers applied with an {@code in} operation
 * @param formerStudentIds optional former-student identifiers applied with an {@code in} operation
 * @param statuses optional enrollment statuses applied with an {@code in} operation
 * @param dateFrom optional lower timestamp bound for enrollment lifecycle fields
 * @param dateTo optional upper timestamp bound for enrollment lifecycle fields
 * @param periodFrom optional lower period bound applied to start and due dates
 * @param periodTo optional upper period bound applied to start and due dates
 */
public record EnrollmentComplexSearchRequest(
    List<@UuidV7 UUID> projectIds,
    List<@UuidV7 UUID> formerStudentIds,
    List<EnrollmentStatus> statuses,
    OffsetDateTime dateFrom,
    OffsetDateTime dateTo,
    LocalDate periodFrom,
    LocalDate periodTo) {

  /** Normalizes nullable list inputs to immutable empty lists. */
  public EnrollmentComplexSearchRequest {
    projectIds = projectIds == null ? List.of() : List.copyOf(projectIds);
    formerStudentIds = formerStudentIds == null ? List.of() : List.copyOf(formerStudentIds);
    statuses = statuses == null ? List.of() : List.copyOf(statuses);
  }
}
