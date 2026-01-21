package com.pug.partner.infra.read.dtos;

import com.pug.geo.infra.read.dtos.CityView;
import java.util.UUID;

/**
 * View representation of an Entity.
 *
 * @param id the unique identifier of the entity
 * @param cnpj the CNPJ of the entity
 * @param name the name of the entity
 * @param address the address of the entity
 * @param city the city view associated with the entity
 */
public record EntityView(UUID id, String cnpj, String name, String address, CityView city) {}
