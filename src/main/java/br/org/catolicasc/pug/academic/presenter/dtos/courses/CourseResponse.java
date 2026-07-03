/*
 * Copyright (c) 2026 Mateus Fernandes and Plataforma Universidade Gratuita.
 * All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized use,
 * copying, modification, distribution, or deployment is prohibited.
 */

package br.org.catolicasc.pug.academic.presenter.dtos.courses;

import br.org.catolicasc.pug.academic.presenter.dtos.areasofexpertise.AreaOfExpertiseResponse;
import br.org.catolicasc.pug.shared.presenter.dtos.AuditInfoResponse;
import java.util.UUID;

/**
 * Data Transfer Object (DTO) used as the standardized API JSON response for Course data.
 *
 * <p>This record consolidates the backend course data along with a nested representation of its
 * parent area of expertise into a single, flattened response optimized for the presentation layer.
 *
 * @param id the unique identifier (UUIDv7) of the academic course
 * @param name the name of the academic course
 * @param areaOfExpertise the nested, client-facing projection of the area of expertise that offers
 *     this course
 * @param auditInfo the nested audit information containing creation and update timestamps
 */
public record CourseResponse(
    UUID id, String name, AreaOfExpertiseResponse areaOfExpertise, AuditInfoResponse auditInfo) {}
