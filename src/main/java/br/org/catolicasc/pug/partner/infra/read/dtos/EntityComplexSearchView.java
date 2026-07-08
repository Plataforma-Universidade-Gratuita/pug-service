package br.org.catolicasc.pug.partner.infra.read.dtos;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Read-only projection used by partner-entity complex-search flows.
 *
 * <p>This view resolves the partner entity together with the linked city data in a single query so
 * the presenter layer can respond without additional geo lookups.
 *
 * @param id the unique identifier (UUIDv7) of the partner entity
 * @param cnpj the raw 14-digit numeric CNPJ string
 * @param name the registered name of the partner entity
 * @param address the physical street address of the partner entity
 * @param cityId the unique identifier (UUIDv7) of the associated city
 * @param cityName the display name of the associated city
 * @param cityIbgeCode the official IBGE code of the associated city
 * @param createdAt the exact timestamp when the entity record was created
 * @param updatedAt the exact timestamp when the entity record was last modified
 */
public record EntityComplexSearchView(
    UUID id,
    String cnpj,
    String name,
    String address,
    UUID cityId,
    String cityName,
    String cityIbgeCode,
    OffsetDateTime createdAt,
    OffsetDateTime updatedAt) {}
