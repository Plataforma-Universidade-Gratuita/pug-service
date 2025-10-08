package com.pug.academic.usecase.student.read;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record ReadStudentByUserRoleIdQuery(@NotNull UUID userRoleId) {}
