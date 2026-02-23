package com.pug.partner.infra.read.dtos;

import com.pug.geo.infra.read.dtos.CityView;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Immutable DTO representing the read-side view of an Entity. This record is used to transfer data
 *
 * @param id the unique identifier of the Entity
 * @param cnpj the CNPJ number of the Entity, stored as a raw string (14 digits, no formatting)
 * @param name the name of the Entity
 * @param address the address of the Entity
 * @param city the CityView associated with the Entity, containing city details
 * @param createdAt the timestamp when the Entity was created
 * @param updatedAt the timestamp when the Entity was last updated
 */
public record EntityView(
    UUID id,
    String cnpj,
    String name,
    String address,
    CityView city,
    OffsetDateTime createdAt,
    OffsetDateTime updatedAt) {}
