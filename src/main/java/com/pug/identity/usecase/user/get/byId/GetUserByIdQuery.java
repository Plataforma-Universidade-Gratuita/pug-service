package com.pug.identity.usecase.user.get.byId;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record GetUserByIdQuery(@NotNull UUID id) {}
