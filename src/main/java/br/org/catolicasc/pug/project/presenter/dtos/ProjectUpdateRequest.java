package br.org.catolicasc.pug.project.presenter.dtos;

import br.org.catolicasc.pug.project.domain.enums.ProjectStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

/**
 * Data Transfer Object (DTO) used as the JSON request payload for partially updating an existing
 * Project.
 *
 * @param description the new project description, or {@code null} to leave unchanged
 * @param maxParticipants the new participant capacity limit, or {@code null} to leave unchanged
 * @param name the new name of the project, or {@code null} to leave unchanged
 * @param offeredHours the new offered hours, or {@code null} to leave unchanged
 * @param status the new lifecycle status, or {@code null} to leave the current status unchanged
 */
public record ProjectUpdateRequest(
    @Size(max = 150) String name,
    @Size(max = 4000) String description,
    Integer maxParticipants,
    @DecimalMin("0.00") BigDecimal offeredHours,
    ProjectStatus status) {}
