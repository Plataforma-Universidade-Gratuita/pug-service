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
