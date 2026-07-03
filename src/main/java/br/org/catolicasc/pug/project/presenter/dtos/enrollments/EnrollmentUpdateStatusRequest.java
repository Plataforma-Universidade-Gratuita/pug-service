/*
 * Copyright (c) 2026 Mateus Fernandes and Plataforma Universidade Gratuita.
 * All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized use,
 * copying, modification, distribution, or deployment is prohibited.
 */

package br.org.catolicasc.pug.project.presenter.dtos.enrollments;

import br.org.catolicasc.pug.project.domain.enums.EnrollmentStatus;
import jakarta.validation.constraints.NotNull;

/** Request payload used to update a single enrollment status. */
public record EnrollmentUpdateStatusRequest(@NotNull EnrollmentStatus status) {}
