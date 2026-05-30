package br.org.catolicasc.pug.partner.presenter.dtos.entities;

import br.org.catolicasc.pug.geo.presenter.dtos.CityResponse;
import br.org.catolicasc.pug.shared.presenter.dtos.AuditInfoResponse;
import java.util.UUID;

/**
 * Response DTO used by partner-entity complex-search flows.
 *
 * @param id the unique identifier (UUIDv7) of the partner entity
 * @param cnpj the raw 14-digit numeric CNPJ string
 * @param cnpjFormatted the CNPJ string formatted with standard punctuation
 * @param name the registered name of the partner entity
 * @param address the physical street address of the partner entity
 * @param city the nested city resolved by the search query
 * @param auditInfo the nested audit information containing creation and update timestamps
 */
public record EntityComplexSearchResponse(
    UUID id,
    String cnpj,
    String cnpjFormatted,
    String name,
    String address,
    CityResponse city,
    AuditInfoResponse auditInfo) {}
