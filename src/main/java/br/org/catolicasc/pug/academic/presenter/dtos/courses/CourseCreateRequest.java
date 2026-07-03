/*
 * Copyright (c) 2026 Mateus Fernandes and Plataforma Universidade Gratuita.
 * All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized use,
 * copying, modification, distribution, or deployment is prohibited.
 */

package br.org.catolicasc.pug.academic.presenter.dtos.courses;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;

/**
 * Data Transfer Object (DTO) used as the JSON request payload for creating a new Academic Course.
 *
 * <p>This record applies Jakarta Bean Validation constraints to ensure the initial data is
 * structurally sound before it reaches the application service layer.
 *
 * @param name the raw name of the academic course (must not be blank and max 120 characters)
 * @param areaOfExpertiseId the unique identifier (UUID) of the area of expertise offering the
 *     course (must not be null)
 */
public record CourseCreateRequest(
    @NotBlank @Size(max = 120) String name, @NotNull UUID areaOfExpertiseId) {}
