package com.pug.academic.usecase.student.create;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;

public record CreateStudentCommand(
    @NotNull UUID userRoleId,
    @NotBlank @Size(max = 15) String academicRegistration,
    @NotNull UUID courseId) {}
