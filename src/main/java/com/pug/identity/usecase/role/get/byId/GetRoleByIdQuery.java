package com.pug.identity.usecase.role.get.byId;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record GetRoleByIdQuery(@NotNull UUID id) {}
