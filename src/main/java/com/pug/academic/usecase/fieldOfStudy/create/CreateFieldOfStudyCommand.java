package com.pug.academic.usecase.fieldOfStudy.create;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateFieldOfStudyCommand(@NotBlank @Size(max = 100) String name) {}
