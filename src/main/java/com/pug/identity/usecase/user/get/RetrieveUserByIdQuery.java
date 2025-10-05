package com.pug.identity.usecase.user.get;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record RetrieveUserByIdQuery(@NotNull UUID id) {}
