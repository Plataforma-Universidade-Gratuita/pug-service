/*
 * Copyright (c) 2026 Mateus Fernandes and Plataforma Universidade Gratuita.
 * All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized use,
 * copying, modification, distribution, or deployment is prohibited.
 */

package br.org.catolicasc.pug.project.presenter.dtos.projects;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.UUID;

/**
 * Data Transfer Object (DTO) used as the JSON request payload for creating a new Project.
 *
 * @param description the detailed description of the project's objectives (max 4000 chars)
 * @param entityId the unique identifier of the partner organization offering the project
 * @param maxParticipants the maximum number of formerStudents allowed to enroll (optional, min 0)
 * @param name the title or name of the project (must not be blank, max 150 chars)
 * @param offeredHours the total counterpart hours the project offers (must be min 0)
 */
public record ProjectCreateRequest(
    @NotBlank @Size(max = 150) String name,
    @NotNull UUID entityId,
    @Size(max = 4000) String description,
    @Min(0) Integer maxParticipants,
    @NotNull @DecimalMin("0.00") BigDecimal offeredHours) {}
