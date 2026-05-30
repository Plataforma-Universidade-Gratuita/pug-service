package br.org.catolicasc.pug.project.presenter.dtos.enrollments;

import br.org.catolicasc.pug.project.domain.enums.EnrollmentStatus;

public record EnrollmentStatusResponse(EnrollmentStatus status, String statusFormatted) {}
