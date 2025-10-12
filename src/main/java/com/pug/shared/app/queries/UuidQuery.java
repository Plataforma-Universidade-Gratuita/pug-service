package com.pug.shared.app.queries;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record UuidQuery(@NotNull UUID id) {}
