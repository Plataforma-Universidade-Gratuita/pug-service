package br.org.catolicasc.pug.project.presenter.dtos;

import br.org.catolicasc.pug.project.domain.enums.EnrollmentStatus;
import jakarta.validation.constraints.NotNull;

/**
 * Data Transfer Object (DTO) used as the JSON request payload for transitioning an existing
 * Enrollment.
 *
 * @param status the target enrollment status to apply
 */
public record EnrollmentUpdateRequest(@NotNull EnrollmentStatus status) {}
