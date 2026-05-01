package br.org.catolicasc.pug.project.presenter.dtos;

import br.org.catolicasc.pug.shared.validation.UuidV7;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

/**
 * Data Transfer Object (DTO) used as the JSON request payload for creating an Enrollment.
 *
 * <p>This request shape is retained for compatibility with supporting tests and builders. The
 * current public enrollment creation endpoint receives the project identifier from the request
 * path.
 *
 * @param projectId the unique identifier of the target project
 */
public record EnrollmentCreateRequest(@NotNull @UuidV7 UUID projectId) {}
