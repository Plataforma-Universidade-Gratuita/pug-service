package com.pug.partner.usecase.entity.update;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record UpdateIsActiveCommand(@NotNull UUID id) {}
