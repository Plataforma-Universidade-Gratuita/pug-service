/*
 * Copyright (c) 2026 Mateus Fernandes and Plataforma Universidade Gratuita.
 * All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized use,
 * copying, modification, distribution, or deployment is prohibited.
 */

package br.org.catolicasc.pug.project.presenter.dtos.enrollments;

import br.org.catolicasc.pug.project.domain.enums.EnrollmentStatus;

/**
 * Nested response describing the raw and localized enrollment status.
 *
 * <p>This wrapper keeps the enum value and its translated label together so clients do not need to
 * infer display text on their own.
 */
public record EnrollmentStatusResponse(EnrollmentStatus status, String statusFormatted) {}
