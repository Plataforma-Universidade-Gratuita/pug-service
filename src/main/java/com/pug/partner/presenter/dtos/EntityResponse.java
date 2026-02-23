package com.pug.partner.presenter.dtos;

import com.pug.geo.presenter.dtos.CityResponse;
import com.pug.shared.presenter.dtos.AuditInfoResponse;

import java.util.UUID;

/**
 * DTO representing the response for an entity. It contains all the necessary information about an
 *
 * @param id            the unique identifier of the entity
 * @param cnpj          the CNPJ number of the entity
 * @param cnpjFormatted the formatted CNPJ number of the entity
 * @param name          the name of the entity
 * @param address       the address of the entity
 * @param city          the city information of the entity
 * @param auditInfo     the audit information of the entity, including creation and update timestamps
 */
public record EntityResponse(
        UUID id, String cnpj, String cnpjFormatted, String name, String address, CityResponse city,
        AuditInfoResponse auditInfo) {
}
