package com.pug.project.service.dtos;

import java.math.BigDecimal;

/**
 * Data Transfer Object (DTO) acting as an application command to update an existing Project.
 *
 * <p>This record encapsulates the requested state changes for a project. The fields are treated as
 * optional for partial updates; omitting a value (or providing {@code null}) will retain the
 * current state in the database.
 *
 * @param description the new description text, or {@code null} to leave unchanged
 * @param maxParticipants the new participant capacity limit, or {@code null} to leave unchanged
 * @param name the new name of the project, or {@code null} to leave unchanged
 * @param offeredHours the new offered hours, or {@code null} to leave unchanged
 */
public record ProjectUpdateCommand(
    String name, String description, Integer maxParticipants, BigDecimal offeredHours) {}
