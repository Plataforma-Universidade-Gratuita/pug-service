package br.org.catolicasc.pug.project.service.dtos.projects;

import br.org.catolicasc.pug.project.domain.enums.ProjectStatus;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Service-layer criteria DTO that encapsulates the optional filters accepted by the project
 * complex-search flow.
 *
 * @param name optional project-name filter applied with a contains-style match
 * @param entityIds optional partner-entity identifiers applied with an {@code in} operation
 * @param description optional project-description filter applied with a contains-style match
 * @param createdByIds optional creator-account identifiers applied with an {@code in} operation
 * @param dateFrom optional lower timestamp boundary applied to the project's timestamp fields
 * @param dateTo optional upper timestamp boundary applied to the project's timestamp fields
 * @param statuses optional lifecycle statuses applied with an {@code in} operation
 * @param maxOfferedHours optional upper boundary for offered counterpart hours
 * @param minOfferedHours optional lower boundary for offered counterpart hours
 */
public record ProjectComplexSearchCriteria(
    String name,
    List<UUID> entityIds,
    String description,
    List<UUID> createdByIds,
    OffsetDateTime dateFrom,
    OffsetDateTime dateTo,
    List<ProjectStatus> statuses,
    BigDecimal maxOfferedHours,
    BigDecimal minOfferedHours) {

  public ProjectComplexSearchCriteria {
    entityIds = entityIds == null ? List.of() : List.copyOf(entityIds);
    createdByIds = createdByIds == null ? List.of() : List.copyOf(createdByIds);
    statuses = statuses == null ? List.of() : List.copyOf(statuses);
  }
}
