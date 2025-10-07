package com.pug.identity.usecase.role.update;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record UpdateIsActiveCommand(@NotNull UUID id) {}
