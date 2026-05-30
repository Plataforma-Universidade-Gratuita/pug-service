package br.org.catolicasc.pug.project.presenter.dtos;

import br.org.catolicasc.pug.project.domain.enums.EnrollmentStatus;

public record EnrollmentStatusResponse(EnrollmentStatus status, String statusFormatted) {}
