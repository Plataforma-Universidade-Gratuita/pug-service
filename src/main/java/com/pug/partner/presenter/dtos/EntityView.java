package com.pug.partner.presenter.dtos;

import com.pug.geo.presenter.dtos.CityResponse;
import java.util.UUID;

/** Projection for Entity with embedded City model. */
public record EntityView(UUID id, String cnpj, String name, String address, CityResponse city) {}
