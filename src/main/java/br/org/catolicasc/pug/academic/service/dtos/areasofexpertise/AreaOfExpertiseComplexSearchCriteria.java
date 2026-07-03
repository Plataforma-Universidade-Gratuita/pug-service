/*
 * Copyright (c) 2026 Mateus Fernandes and Plataforma Universidade Gratuita.
 * All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized use,
 * copying, modification, distribution, or deployment is prohibited.
 */

package br.org.catolicasc.pug.academic.service.dtos.areasofexpertise;

/**
 * Service-layer criteria DTO used to execute area-of-expertise complex-search operations.
 *
 * @param name optional area-of-expertise name fragment used in a {@code like} filter
 */
public record AreaOfExpertiseComplexSearchCriteria(String name) {}
