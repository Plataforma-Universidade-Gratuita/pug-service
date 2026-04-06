package com.pug.project.presenter.dtos;

import com.pug.shared.validation.UuidV7;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

/**
 * Data Transfer Object (DTO) used as the JSON request payload for creating a new Enrollment.
 *
 * <p>This request only carries the target {@code projectId}. The {@code studentId} is not provided
 * by the client; instead, it is resolved server-side from the currently authenticated user (for
 * example, via {@code AuthService.getCurrentAccountId()}), ensuring that a student can only create
 * enrollments on their own behalf.
 *
 * @param projectId the unique identifier (UUIDv7) of the project the currently authenticated
 *     student wishes to enroll in
 */
public record EnrollmentCreateRequest(@NotNull @UuidV7 UUID projectId) {}
