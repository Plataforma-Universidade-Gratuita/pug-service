package br.org.catolicasc.pug.project.presenter.dtos;

import br.org.catolicasc.pug.project.domain.enums.EnrollmentStatus;
import br.org.catolicasc.pug.shared.validation.UuidV7;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record EnrollmentComplexSearchRequest(
    List<@UuidV7 UUID> projectIds,
    List<@UuidV7 UUID> formerStudentIds,
    List<EnrollmentStatus> statuses,
    OffsetDateTime dateFrom,
    OffsetDateTime dateTo,
    LocalDate periodFrom,
    LocalDate periodTo) {

  public EnrollmentComplexSearchRequest {
    projectIds = projectIds == null ? List.of() : List.copyOf(projectIds);
    formerStudentIds = formerStudentIds == null ? List.of() : List.copyOf(formerStudentIds);
    statuses = statuses == null ? List.of() : List.copyOf(statuses);
  }
}
