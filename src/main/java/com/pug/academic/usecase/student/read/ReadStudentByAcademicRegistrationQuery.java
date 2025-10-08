package com.pug.academic.usecase.student.read;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ReadStudentByAcademicRegistrationQuery(
    @NotBlank @Size(max = 15) String academicRegistration) {}
