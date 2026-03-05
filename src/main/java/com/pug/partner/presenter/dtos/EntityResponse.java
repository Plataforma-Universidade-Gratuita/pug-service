package com.pug.partner.presenter.dtos;

import com.pug.geo.presenter.dtos.CityResponse;
import com.pug.shared.presenter.dtos.AuditInfoResponse;
import java.util.UUID;

/**
 * Data Transfer Object (DTO) used as the standardized API JSON response for Partner Entities.
 *
 * <p>This record consolidates the backend entity data along with its resolved geographical location
 * (City) into a single, flattened response optimized for the presentation layer. It includes
 * pre-formatted strings for direct UI rendering.
 *
 * @param id the unique identifier (UUIDv7) of the partner entity
 * @param cnpj the raw 14-digit numeric CNPJ string
 * @param cnpjFormatted the CNPJ string formatted with standard punctuation (e.g.,
 *     "00.000.000/0000-00")
 * @param name the registered name or corporate reason of the partner entity
 * @param address the physical street address of the entity
 * @param city the nested, client-facing projection of the city where the entity is located
 * @param auditInfo the nested audit information containing creation and update timestamps
 */
public record EntityResponse(
    UUID id,
    String cnpj,
    String cnpjFormatted,
    String name,
    String address,
    CityResponse city,
    AuditInfoResponse auditInfo) {}
