package com.pug.identity.usecase.role.read;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record ReadRoleByIdQuery(@NotNull UUID id) {}
