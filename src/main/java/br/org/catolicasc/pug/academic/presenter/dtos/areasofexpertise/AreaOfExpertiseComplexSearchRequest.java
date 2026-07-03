/*
 * Copyright (c) 2026 Mateus Fernandes and Plataforma Universidade Gratuita.
 * All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized use,
 * copying, modification, distribution, or deployment is prohibited.
 */

package br.org.catolicasc.pug.academic.presenter.dtos.areasofexpertise;

import jakarta.validation.constraints.Pattern;

/**
 * Request DTO used by the area-of-expertise complex-search endpoint.
 *
 * @param name optional area-of-expertise name fragment used in a {@code like} filter
 */
public record AreaOfExpertiseComplexSearchRequest(@Pattern(regexp = ".*\\S.*") String name) {}
