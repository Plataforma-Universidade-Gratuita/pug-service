package br.org.catolicasc.pug.project.service.dtos.enrollments;

import java.util.UUID;

/**
 * Command object used to request enrollment creation for a project and former student.
 *
 * @param projectId the target project identifier
 * @param formerStudentId the optional former-student identifier explicitly selected by an admin
 */
public record EnrollmentCreateCommand(UUID projectId, UUID formerStudentId) {}
