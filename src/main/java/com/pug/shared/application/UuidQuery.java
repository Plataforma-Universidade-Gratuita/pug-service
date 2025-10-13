package com.pug.shared.application;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record UuidQuery(@NotNull UUID id) {}
