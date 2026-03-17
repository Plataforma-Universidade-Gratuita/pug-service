package com.pug.project.service.dtos;

import java.util.UUID;

/** Data Transfer Object (DTO) acting as an application command to request a new Enrollment. */
public record EnrollmentCreateCommand(UUID projectId, UUID studentId) {}
