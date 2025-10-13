package com.pug.partner.service.queries;

import com.pug.shared.infra.persistence.PageRequest;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record ListEntitiesByCityQuery(@NotNull UUID cityId, @NotNull PageRequest pageRequest) {}
