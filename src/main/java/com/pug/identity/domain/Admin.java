package com.pug.identity.domain;

import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.UUID;

public record Admin(@NotNull UUID userId, @NotNull Instant grantedAt) {}
