package com.pug.identity.usecase.user.read;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record ReadUserByIdQuery(@NotNull UUID id) {}
