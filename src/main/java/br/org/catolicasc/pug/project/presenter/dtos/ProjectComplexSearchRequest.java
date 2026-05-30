package br.org.catolicasc.pug.project.presenter.dtos;

import br.org.catolicasc.pug.project.domain.enums.ProjectStatus;
import br.org.catolicasc.pug.shared.validation.UuidV7;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Request DTO used by the paginated complex-search endpoint for projects.
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
public record ProjectComplexSearchRequest(
    @Size(min = 1, max = 150) String name,
    List<@UuidV7 UUID> entityIds,
    @Size(min = 1, max = 4000) String description,
    List<@UuidV7 UUID> createdByIds,
    OffsetDateTime dateFrom,
    OffsetDateTime dateTo,
    List<ProjectStatus> statuses,
    @DecimalMin("0.00") BigDecimal maxOfferedHours,
    @DecimalMin("0.00") BigDecimal minOfferedHours) {

  public ProjectComplexSearchRequest {
    entityIds = entityIds == null ? List.of() : List.copyOf(entityIds);
    createdByIds = createdByIds == null ? List.of() : List.copyOf(createdByIds);
    statuses = statuses == null ? List.of() : List.copyOf(statuses);
  }
}
