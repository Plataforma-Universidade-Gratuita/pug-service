package br.org.catolicasc.pug.project.service;

import br.org.catolicasc.pug.project.domain.Enrollment;
import br.org.catolicasc.pug.project.domain.enums.EnrollmentStatus;
import br.org.catolicasc.pug.project.domain.vos.EnrollmentIdentifier;
import br.org.catolicasc.pug.project.service.dtos.enrollments.EnrollmentCreateCommand;
import java.util.UUID;

/**
 * Application-layer command contract for enrollment lifecycle operations.
 *
 * <p>This boundary centralizes the command-side rules around enrollment creation, deletion,
 * explicit status transitions, and bulk propagation flows triggered by project or former-student
 * lifecycle changes.
 */
public interface EnrollmentsService {

  Enrollment changeStatus(EnrollmentIdentifier identifier, EnrollmentStatus status);

  long changeStatusByProjectId(UUID projectId, EnrollmentStatus targetStatus);

  long changeStatusByProjectId(
      UUID projectId, EnrollmentStatus currentStatus, EnrollmentStatus targetStatus);

  long completeAllByFormerStudentId(UUID formerStudentId);

  boolean delete(EnrollmentIdentifier identifier);

  boolean existsAnyByProjectId(UUID projectId);

  boolean existsAnyByFormerStudentId(UUID formerStudentId);

  Enrollment getByIds(EnrollmentIdentifier identifier);

  Enrollment save(EnrollmentCreateCommand cmd);
}
