package com.pug.partner.infra.read.dtos;

import com.pug.geo.infra.read.dtos.CityView;
import java.util.UUID;

/** Projection for Entity with embedded City model. */
public record EntityView(UUID id, String cnpj, String name, String address, CityView city) {}
