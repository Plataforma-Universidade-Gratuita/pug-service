package br.org.catolicasc.pug.project.presenter.dtos.enrollments;

import br.org.catolicasc.pug.project.domain.enums.EnrollmentStatus;
import jakarta.validation.constraints.NotNull;

public record EnrollmentUpdateStatusRequest(@NotNull EnrollmentStatus status) {}
