package com.pug.geo.usecase.get.byIbgeCode;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record GetCityByIbgeCodeQuery(
    @NotBlank @Size(min = 7, max = 7) @Pattern(regexp = "\\d{7}") String ibgeCode) {}
