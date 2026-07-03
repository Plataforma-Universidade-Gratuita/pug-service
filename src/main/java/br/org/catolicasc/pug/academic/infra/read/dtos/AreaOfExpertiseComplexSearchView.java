/*
 * Copyright (c) 2026 Mateus Fernandes and Plataforma Universidade Gratuita.
 * All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized use,
 * copying, modification, distribution, or deployment is prohibited.
 */

package br.org.catolicasc.pug.academic.infra.read.dtos;

import java.util.UUID;

/**
 * Lightweight read projection representing an area of expertise in complex-search responses.
 *
 * @param id the unique identifier of the area of expertise
 * @param name the canonical area-of-expertise name
 */
public record AreaOfExpertiseComplexSearchView(UUID id, String name) {}
