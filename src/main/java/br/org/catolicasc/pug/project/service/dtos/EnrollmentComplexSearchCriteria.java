package br.org.catolicasc.pug.project.service.dtos;

import br.org.catolicasc.pug.project.domain.enums.EnrollmentStatus;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record EnrollmentComplexSearchCriteria(
    List<UUID> projectIds,
    List<UUID> studentIds,
    List<EnrollmentStatus> statuses,
    OffsetDateTime dateFrom,
    OffsetDateTime dateTo,
    LocalDate periodFrom,
    LocalDate periodTo) {

  public EnrollmentComplexSearchCriteria {
    projectIds = projectIds == null ? List.of() : List.copyOf(projectIds);
    studentIds = studentIds == null ? List.of() : List.copyOf(studentIds);
    statuses = statuses == null ? List.of() : List.copyOf(statuses);
  }
}
