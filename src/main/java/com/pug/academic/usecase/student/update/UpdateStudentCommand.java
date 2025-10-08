package com.pug.academic.usecase.student.update;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;

public record UpdateStudentCommand(
    @NotNull UUID id,
    @NotBlank @Size(max = 15) String academicRegistration,
    @NotNull UUID courseId) {}
