package br.org.catolicasc.pug.partner.service.dtos;

import java.util.UUID;

/**
 * Data Transfer Object (DTO) acting as an application command to update an existing Partner Entity.
 *
 * <p>This record encapsulates the requested state changes for a partner organization. The fields
 * are treated as optional for partial updates; omitting a value will retain the current state in
 * the database.
 *
 * @param name the new name of the organization, or {@code null} to leave unchanged
 * @param cityId the new city ID, or {@code null} to leave unchanged
 * @param address the new physical street address, or {@code null} to leave unchanged
 */
public record EntityUpdateCommand(String name, UUID cityId, String address) {}
