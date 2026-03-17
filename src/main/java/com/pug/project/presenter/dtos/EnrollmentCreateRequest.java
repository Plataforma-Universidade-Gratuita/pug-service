package com.pug.project.presenter.dtos;

import com.pug.shared.validation.UuidV7;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record EnrollmentCreateRequest(
    @NotNull @UuidV7 UUID projectId, @NotNull @UuidV7 UUID studentId) {}
