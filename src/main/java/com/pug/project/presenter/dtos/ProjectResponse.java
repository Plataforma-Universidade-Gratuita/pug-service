package com.pug.project.presenter.dtos;

import com.pug.academic.presenter.dtos.SchoolResponse;
import com.pug.identity.presenter.dtos.AccountResponse;
import com.pug.partner.presenter.dtos.EntityResponse;
import com.pug.project.domain.enums.ProjectStatus;
import com.pug.shared.presenter.dtos.AuditInfoResponse;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Data Transfer Object (DTO) used as the standardized API JSON response for Projects.
 *
 * @param id the unique identifier (UUIDv7) of the project
 * @param name the title or name of the project
 * @param entity the nested read-only projection of the partner organization
 * @param school the nested read-only projection of the associated school
 * @param description the project description
 * @param createdBy the nested read-only projection of the staff creator
 * @param maxParticipants the maximum number of students allowed
 * @param offeredHours the total counterpart hours offered
 * @param status the current execution state
 * @param statusFormatted the localized project status string
 * @param closedAt the exact timestamp when the project was closed
 * @param closedAtFormatted the localized closure date
 * @param auditInfo the nested audit information
 */
public record ProjectResponse(
    UUID id,
    String name,
    EntityResponse entity,
    SchoolResponse school,
    String description,
    AccountResponse createdBy,
    Integer maxParticipants,
    BigDecimal offeredHours,
    ProjectStatus status,
    String statusFormatted,
    OffsetDateTime closedAt,
    String closedAtFormatted,
    AuditInfoResponse auditInfo) {}
