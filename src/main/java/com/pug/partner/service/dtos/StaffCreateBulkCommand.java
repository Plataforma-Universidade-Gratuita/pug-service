package com.pug.partner.service.dtos;

import com.pug.identity.service.dtos.CreateAccountCommand;

import java.util.UUID;

/**
 * Command to create bulk staff members for a specific entity.
 *
 * @param accountCommands the commands containing the data to create the underlying accounts.
 * @param entityId        the ID of the entity to which the staff members will be assigned.
 */
public record StaffCreateBulkCommand(
        Iterable<CreateAccountCommand> accountCommands,
        UUID entityId
) {
}
