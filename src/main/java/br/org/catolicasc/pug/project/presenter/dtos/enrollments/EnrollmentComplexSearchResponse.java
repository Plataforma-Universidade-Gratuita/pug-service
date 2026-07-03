/*
 * Copyright (c) 2026 Mateus Fernandes and Plataforma Universidade Gratuita.
 * All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized use,
 * copying, modification, distribution, or deployment is prohibited.
 */

package br.org.catolicasc.pug.project.presenter.dtos.enrollments;

import br.org.catolicasc.pug.academic.presenter.dtos.formerstudents.FormerStudentSimpleComplexSearchResponse;
import br.org.catolicasc.pug.project.presenter.dtos.projects.ProjectSimpleComplexSearchResponse;

/**
 * Response item returned by the enrollment complex-search endpoint.
 *
 * <p>This shape keeps the project and former-student references nested while surfacing the
 * enrollment lifecycle details needed by administrative filtering screens.
 */
public record EnrollmentComplexSearchResponse(
    ProjectSimpleComplexSearchResponse project,
    FormerStudentSimpleComplexSearchResponse student,
    EnrollmentStatusResponse status,
    EnrollmentInfoResponse enrollmentInfo) {}
