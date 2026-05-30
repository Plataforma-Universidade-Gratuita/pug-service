package br.org.catolicasc.pug.project.service.dtos.enrollments;

import java.util.UUID;

public record EnrollmentCreateCommand(UUID projectId, UUID formerStudentId) {}
