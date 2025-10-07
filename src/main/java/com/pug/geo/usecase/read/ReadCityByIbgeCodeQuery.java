package com.pug.geo.usecase.read;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ReadCityByIbgeCodeQuery(
    @NotBlank @Size(min = 7, max = 7) @Pattern(regexp = "\\d{7}") String ibgeCode) {}
