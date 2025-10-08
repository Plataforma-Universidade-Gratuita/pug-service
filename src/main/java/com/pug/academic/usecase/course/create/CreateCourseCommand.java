package com.pug.academic.usecase.course.create;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;

public record CreateCourseCommand(@NotBlank @Size(max = 120) String name, @NotNull UUID fieldId) {}
