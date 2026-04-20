package br.org.catolicasc.pug.partner.service.dtos;

import br.org.catolicasc.pug.partner.domain.Entity;

import java.util.UUID;

/**
 * Data Transfer Object (DTO) acting as an application command to provision a new Partner Entity.
 *
 * <p>This record encapsulates the raw input data required by the application service to instantiate
 * a new {@link Entity} aggregate.
 *
 * @param cnpjString the raw 14-digit numeric CNPJ string belonging to the organization
 * @param name the registered name or corporate reason of the organization
 * @param cityId the unique identifier of the city where the organization is located
 * @param address the physical street address
 */
public record EntityCreateCommand(String cnpjString, String name, UUID cityId, String address) {}
