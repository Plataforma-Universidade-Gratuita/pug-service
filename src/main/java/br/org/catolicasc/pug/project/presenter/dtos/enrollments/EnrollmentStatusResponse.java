package br.org.catolicasc.pug.project.presenter.dtos.enrollments;

import br.org.catolicasc.pug.project.domain.enums.EnrollmentStatus;

/** Nested response describing the raw and localized enrollment status. */
public record EnrollmentStatusResponse(EnrollmentStatus status, String statusFormatted) {}
