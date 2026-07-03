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
 * Lightweight course projection used by former-student complex-search queries.
 *
 * @param id course identifier
 * @param name course name
 * @param areaOfExpertise lightweight areaOfExpertise projection associated with the course
 */
public record CourseComplexSearchView(
    UUID id, String name, AreaOfExpertiseComplexSearchView areaOfExpertise) {}
