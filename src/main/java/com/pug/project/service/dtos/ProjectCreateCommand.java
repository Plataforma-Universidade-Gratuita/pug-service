package com.pug.project.service.dtos;

import java.math.BigDecimal;
import java.util.UUID;

/** Data Transfer Object (DTO) acting as an application command to provision a new Project. */
public record ProjectCreateCommand(
    String name,
    UUID entityId,
    String description,
    UUID createdBy,
    Integer maxParticipants,
    BigDecimal offeredHours) {}
