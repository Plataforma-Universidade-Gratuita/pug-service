package br.org.catolicasc.pug.project.service;

import br.org.catolicasc.pug.project.domain.Enrollment;
import br.org.catolicasc.pug.project.domain.enums.EnrollmentStatus;
import br.org.catolicasc.pug.project.domain.vos.EnrollmentIdentifier;
import br.org.catolicasc.pug.project.service.dtos.EnrollmentCreateCommand;
import java.util.UUID;

public interface EnrollmentsService {

  Enrollment changeStatus(EnrollmentIdentifier identifier, EnrollmentStatus status);

  long changeStatusByProjectId(UUID projectId, EnrollmentStatus targetStatus);

  long changeStatusByProjectId(
      UUID projectId, EnrollmentStatus currentStatus, EnrollmentStatus targetStatus);

  long completeAllByStudentId(UUID studentId);

  boolean delete(EnrollmentIdentifier identifier);

  boolean existsAnyByProjectId(UUID projectId);

  boolean existsAnyByStudentId(UUID studentId);

  Enrollment getByIds(EnrollmentIdentifier identifier);

  Enrollment save(EnrollmentCreateCommand cmd);
}
