package com.pug.partner.usecase.entity.get;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record RetrievePartnerEntityByIdCommand(@NotNull UUID id) {}
