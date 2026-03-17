package com.pug.project.service.dtos;

import java.math.BigDecimal;

/** Data Transfer Object (DTO) acting as an application command to update an existing Project. */
public record ProjectUpdateCommand(
    String name, String description, Integer maxParticipants, BigDecimal offeredHours) {}
