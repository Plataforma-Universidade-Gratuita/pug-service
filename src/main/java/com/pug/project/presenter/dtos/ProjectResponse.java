package com.pug.project.presenter.dtos;

import com.pug.identity.presenter.dtos.AccountResponse;
import com.pug.partner.presenter.dtos.EntityResponse;
import com.pug.project.domain.enums.ProjectStatus;
import com.pug.shared.presenter.dtos.AuditInfoResponse;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record ProjectResponse(
    UUID id,
    String name,
    EntityResponse entity,
    String description,
    AccountResponse createdBy,
    Integer maxParticipants,
    BigDecimal offeredHours,
    ProjectStatus status,
    String statusFormatted,
    OffsetDateTime closedAt,
    String closedAtFormatted,
    AuditInfoResponse auditInfo) {}
