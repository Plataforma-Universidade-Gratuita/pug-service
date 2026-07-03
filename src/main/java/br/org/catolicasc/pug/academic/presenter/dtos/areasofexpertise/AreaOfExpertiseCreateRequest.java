/*
 * Copyright (c) 2026 Mateus Fernandes and Plataforma Universidade Gratuita.
 * All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized use,
 * copying, modification, distribution, or deployment is prohibited.
 */

package br.org.catolicasc.pug.academic.presenter.dtos.areasofexpertise;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request DTO used as the JSON payload for creating a new academic area of expertise.
 *
 * @param name the raw area-of-expertise name (must not be blank and max 100 characters)
 */
public record AreaOfExpertiseCreateRequest(@NotBlank @Size(max = 100) String name) {}
