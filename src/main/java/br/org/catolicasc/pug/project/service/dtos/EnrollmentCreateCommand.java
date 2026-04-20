package br.org.catolicasc.pug.project.service.dtos;

import br.org.catolicasc.pug.project.domain.Enrollment;
import br.org.catolicasc.pug.project.service.EnrollmentService;
import java.util.UUID;

/**
 * Data Transfer Object (DTO) acting as an application command to request a new {@link Enrollment}.
 *
 * <p>This record encapsulates the minimal data required by the application service to instantiate a
 * new enrollment aggregate, namely the identifier of the target project. The student account to be
 * enrolled is resolved server-side from the current authentication context (for example, via {@code
 * AuthService.getCurrentAccountId()}), ensuring that a student can only create enrollments on their
 * own behalf.
 *
 * <p>This DTO does not perform any validation by itself; structural and business validations are
 * enforced in the {@link EnrollmentService} and within the {@link Enrollment} aggregate.
 *
 * @param projectId the unique identifier (UUID) of the project for which the enrollment is being
 *     requested
 */
public record EnrollmentCreateCommand(UUID projectId) {}
