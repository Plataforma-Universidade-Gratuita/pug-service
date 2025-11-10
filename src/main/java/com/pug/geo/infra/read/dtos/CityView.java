package com.pug.geo.infra.read.dtos;

import java.util.UUID;

/** City view DTO. */
public record CityView(UUID id, String name, String ibgeCode) {}
