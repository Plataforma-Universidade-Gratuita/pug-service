package br.org.catolicasc.pug.project.presenter.dtos.enrollments;

import br.org.catolicasc.pug.academic.presenter.dtos.formerstudents.FormerStudentSimpleComplexSearchResponse;
import br.org.catolicasc.pug.project.presenter.dtos.projects.ProjectSimpleComplexSearchResponse;

public record EnrollmentComplexSearchResponse(
    ProjectSimpleComplexSearchResponse project,
    FormerStudentSimpleComplexSearchResponse student,
    EnrollmentStatusResponse status,
    EnrollmentInfoResponse enrollmentInfo) {}
