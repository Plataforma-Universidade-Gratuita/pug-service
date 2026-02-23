package com.pug.partner.presenter.dtos;

import com.pug.geo.presenter.dtos.CityResponse;
import com.pug.shared.presenter.dtos.AuditInfoResponse;
import java.util.UUID;

/**
 * DTO representing the response for an entityId. It contains all the necessary information about an
 *
 * @param id the unique identifier of the entityId
 * @param cnpj the CNPJ number of the entityId
 * @param cnpjFormatted the formatted CNPJ number of the entityId
 * @param name the name of the entityId
 * @param address the address of the entityId
 * @param city the city information of the entityId
 * @param auditInfo the audit information of the entityId, including creation and update timestamps
 */
public record EntityResponse(
    UUID id,
    String cnpj,
    String cnpjFormatted,
    String name,
    String address,
    CityResponse city,
    AuditInfoResponse auditInfo) {}
