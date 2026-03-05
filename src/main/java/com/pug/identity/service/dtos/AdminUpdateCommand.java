package com.pug.identity.service.dtos;

import com.pug.shared.domain.enums.Campi;

/**
 * Data Transfer Object (DTO) acting as an application command to update an existing Administrator.
 *
 * <p>This record encapsulates the requested state changes for an admin profile. The fields,
 * including the nested account command, are treated as optional for partial updates.
 *
 * @param accountCommand the nested command containing the data to update the underlying account, or
 *     {@code null}
 * @param campus the new university campus assignment, or {@code null} to leave unchanged
 */
public record AdminUpdateCommand(AccountUpdateCommand accountCommand, Campi campus) {}
