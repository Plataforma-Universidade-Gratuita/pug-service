package br.org.catolicasc.pug.project.service.dtos.attendance;

import br.org.catolicasc.pug.project.domain.enums.AttendanceStatus;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Application-layer criteria object used to execute paginated attendance complex-search queries.
 *
 * @param projectIds the optional project identifiers used in an {@code in} filter
 * @param formerStudentIds the optional former student account identifiers used in an {@code in}
 *     filter
 * @param statuses the optional attendance statuses used in an {@code in} filter
 * @param validatedByIds the optional validator account identifiers used in an {@code in} filter
 * @param durationFrom the optional inclusive minimum duration filter
 * @param durationTo the optional inclusive maximum duration filter
 * @param dateFrom the optional inclusive lower timestamp bound
 * @param dateTo the optional inclusive upper timestamp bound
 */
public record AttendanceComplexSearchCriteria(
    List<UUID> projectIds,
    List<UUID> formerStudentIds,
    List<AttendanceStatus> statuses,
    List<UUID> validatedByIds,
    BigDecimal durationFrom,
    BigDecimal durationTo,
    OffsetDateTime dateFrom,
    OffsetDateTime dateTo) {

  /** Normalizes nullable list inputs to immutable empty lists. */
  public AttendanceComplexSearchCriteria {
    projectIds = projectIds == null ? List.of() : List.copyOf(projectIds);
    formerStudentIds = formerStudentIds == null ? List.of() : List.copyOf(formerStudentIds);
    statuses = statuses == null ? List.of() : List.copyOf(statuses);
    validatedByIds = validatedByIds == null ? List.of() : List.copyOf(validatedByIds);
  }
}
