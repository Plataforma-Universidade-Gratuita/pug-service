package com.pug.academic.usecase.fieldOfStudy.update;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;

public record UpdateFieldOfStudyCommand(@NotNull UUID id, @NotBlank @Size(max = 100) String name) {}
