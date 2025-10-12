package com.pug.geo.service.queries;

import com.pug.shared.infra.persistence.PageRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SearchCitiesQuery(
    @NotBlank @Size(min = 2, max = 100) String pattern, @Valid PageRequest pageRequest) {}
