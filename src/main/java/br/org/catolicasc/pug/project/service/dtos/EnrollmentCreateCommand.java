package br.org.catolicasc.pug.project.service.dtos;

import java.util.UUID;

public record EnrollmentCreateCommand(UUID projectId, UUID studentId) {}
