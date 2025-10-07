package com.pug.shared.dtos;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record ReadByIdQuery(@NotNull UUID id) {}
