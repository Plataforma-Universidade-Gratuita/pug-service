package com.pug.partner.infra.read.dtos;

import com.pug.geo.infra.read.dtos.CityView;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Data Transfer Object (DTO) representing a read-only view of a Partner Entity.
 * <p>
 * Following CQRS principles, this record is used exclusively for returning queried data
 * to the client. It aggregates the partner organization's details with a nested projection
 * of its geographic location ({@link CityView}) to provide a comprehensive, lightweight
 * structure optimized for JSON serialization.
 *
 * @param id        the unique identifier (UUIDv7) of the partner entity
 * @param cnpj      the exact 14-digit numeric CNPJ string
 * @param name      the registered name or corporate reason of the entity
 * @param address   the physical street address
 * @param city      the read-only projection of the city where the entity is located
 * @param createdAt the exact timestamp when the entity record was created
 * @param updatedAt the exact timestamp when the entity record was last modified
 */
public record EntityView(
        UUID id,
        String cnpj,
        String name,
        String address,
        CityView city,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt) {
}