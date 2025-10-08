package com.pug.academic.usecase.course.update;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;

public record UpdateCourseCommand(
    @NotNull UUID id, @NotBlank @Size(max = 120) String name, @NotNull UUID fieldId) {}
