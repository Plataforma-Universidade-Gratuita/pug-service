package com.pug.shared.dtos;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ReadByPatternQuery(
    @NotNull String query, @Min(1) @Max(200) Integer limit, @Min(0) Integer offset) {
  public ReadByPatternQuery(@NotNull String query, @Min(1) @Max(200) Integer limit) {
    this(query, limit, null);
  }
}
