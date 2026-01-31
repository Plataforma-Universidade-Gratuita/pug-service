package com.pug.partner.service.dtos;

import com.pug.identity.service.dtos.AccountCreateCommand;
import java.util.UUID;

/**
 * Command DTO for creating a new Staff member.
 *
 * @param entityId the ID of the entity the staff belongs to.
 * @param accountCommand the command for creating the associated account.
 */
public record StaffCreateCommand(UUID entityId, AccountCreateCommand accountCommand) {}
