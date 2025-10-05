package com.pug.identity.usecase.role.get;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record RetrieveRoleByIdQuery(@NotNull UUID id) {}
