package com.pug.geo.usecase.get.byPattern;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ListCitiesByPatternQuery(@NotNull String query, @Min(1) @Max(200) Integer limit) {}
