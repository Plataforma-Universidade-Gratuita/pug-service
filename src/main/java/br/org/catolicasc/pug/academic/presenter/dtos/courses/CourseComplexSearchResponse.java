/*
 * Copyright (c) 2026 Mateus Fernandes and Plataforma Universidade Gratuita.
 * All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized use,
 * copying, modification, distribution, or deployment is prohibited.
 */

package br.org.catolicasc.pug.academic.presenter.dtos.courses;

import br.org.catolicasc.pug.academic.presenter.dtos.areasofexpertise.AreaOfExpertiseComplexSearchResponse;
import java.util.UUID;

/**
 * Lightweight course response used by former-student complex-search payloads.
 *
 * @param id course identifier
 * @param name course name
 * @param areaOfExpertise lightweight area-of-expertise projection associated with the course
 */
public record CourseComplexSearchResponse(
    UUID id, String name, AreaOfExpertiseComplexSearchResponse areaOfExpertise) {}
