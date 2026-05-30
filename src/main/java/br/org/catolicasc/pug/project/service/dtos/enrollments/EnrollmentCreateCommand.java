package br.org.catolicasc.pug.project.service.dtos.enrollments;

import java.util.UUID;

/** Command object used to request enrollment creation for a project and former student. */
public record EnrollmentCreateCommand(UUID projectId, UUID formerStudentId) {}
