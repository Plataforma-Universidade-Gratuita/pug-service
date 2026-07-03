/*
 * Copyright (c) 2026 Mateus Fernandes and Plataforma Universidade Gratuita.
 * All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized use,
 * copying, modification, distribution, or deployment is prohibited.
 */

package br.org.catolicasc.pug.academic.service.dtos.courses;

import br.org.catolicasc.pug.academic.domain.Course;
import java.util.UUID;

/**
 * Data Transfer Object (DTO) acting as an application command to provision a new Course.
 *
 * <p>This record encapsulates the raw input data required by the application service to instantiate
 * a new {@link Course} aggregate.
 *
 * @param name the raw name of the academic course
 * @param areaOfExpertiseId the unique identifier of the areaOfExpertise offering this course
 */
public record CourseCreateCommand(String name, UUID areaOfExpertiseId) {}
