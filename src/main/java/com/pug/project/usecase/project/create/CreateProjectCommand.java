package com.pug.project.usecase.project.create;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.UUID;

public record CreateProjectCommand(
        @NotBlank @Size(max = 150) String name,
        String description,
        UUID entityId,
        UUID fieldId,
        UUID createdById,
        Integer maxParticipants
) {}
