/*
 * Copyright (c) 2026 Mateus Fernandes and Plataforma Universidade Gratuita.
 * All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized use,
 * copying, modification, distribution, or deployment is prohibited.
 */

package br.org.catolicasc.pug.academic.presenter.dtos.areasofexpertise;

import java.util.UUID;

/**
 * Lightweight area-of-expertise response shared by academic complex-search payloads.
 *
 * @param id area-of-expertise identifier
 * @param name area-of-expertise name
 */
public record AreaOfExpertiseComplexSearchResponse(UUID id, String name) {}
