package com.pug.partner.usecase.entity.read;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record ReadPartnerEntityByIdQuery(@NotNull UUID id) {}
