package com.pug.identity.usecase.role.update;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record AttIsActiveCommand(@NotNull UUID id) {}
