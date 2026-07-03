/*
 * Copyright (c) 2026 Mateus Fernandes and Plataforma Universidade Gratuita.
 * All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized use,
 * copying, modification, distribution, or deployment is prohibited.
 */

package br.org.catolicasc.pug.academic.service.dtos.courses;

import java.util.UUID;

/**
 * Data Transfer Object (DTO) acting as an application command to update an existing Course.
 *
 * <p>This record encapsulates the requested state changes for a course. The fields are treated as
 * optional for partial updates; omitting a value will retain the current state in the database.
 *
 * @param name the new name of the course, or {@code null} to leave unchanged
 * @param areaOfExpertiseId the new unique identifier of the areaOfExpertise, or {@code null} to
 *     leave unchanged
 */
public record CourseUpdateCommand(String name, UUID areaOfExpertiseId) {}
