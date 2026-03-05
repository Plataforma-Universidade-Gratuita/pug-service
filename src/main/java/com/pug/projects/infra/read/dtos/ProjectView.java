package com.pug.projects.infra.read.dtos;

import com.pug.identity.infra.read.dtos.AccountView;
import com.pug.partner.infra.read.dtos.EntityView;
import com.pug.projects.domain.enums.ProjectStatus;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Data Transfer Object (DTO) representing a read-only view of a Project.
 * <p>
 * Following CQRS principles, this record is used exclusively for returning queried data
 * to the client. It aggregates the project details with nested projections of the
 * partner organization ({@link EntityView}) and the staff creator ({@link AccountView})
 * to provide a comprehensive structure optimized for JSON serialization.
 *
 * @param id              the unique identifier (UUIDv7) of the project
 * @param name            the title or name of the project
 * @param entity          the nested read-only projection of the partner organization offering the project
 * @param description     the detailed description of the project
 * @param createdBy       the nested read-only projection of the staff account who created the project
 * @param maxParticipants the maximum number of students allowed to enroll
 * @param offeredHours    the total counterpart hours the project offers
 * @param status          the current execution state of the project
 * @param closedAt        the exact timestamp when the project reached a terminal state
 * @param createdAt       the exact timestamp when the project record was created
 * @param updatedAt       the exact timestamp when the project record was last modified
 */
public record ProjectView(
        UUID id,
        String name,
        EntityView entity,
        String description,
        AccountView createdBy,
        Integer maxParticipants,
        BigDecimal offeredHours,
        ProjectStatus status,
        OffsetDateTime closedAt,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt) {
}