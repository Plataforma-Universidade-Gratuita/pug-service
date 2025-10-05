package com.pug.identity.usecase.role.activation;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record ChangeActiveRoleStatusCommand(@NotNull UUID id) {}
