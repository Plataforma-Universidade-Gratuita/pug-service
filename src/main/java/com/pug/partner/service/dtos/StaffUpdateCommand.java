package com.pug.partner.service.dtos;

import com.pug.identity.service.dtos.AccountUpdateCommand;

import java.util.UUID;

/**
 * Command DTO for updating an existing Staff member.
 *
 * @param entityId       the new ID of the entity (optional).
 * @param accountCommand the command for updating the associated account (optional).
 */
public record StaffUpdateCommand(UUID entityId, AccountUpdateCommand accountCommand) {
}