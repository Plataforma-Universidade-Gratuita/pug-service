package com.pug.partner.service.dtos;

import com.pug.identity.service.dtos.CreateAccountCommand;

import java.util.UUID;

/**
 * Command to create a new staff member.
 *
 * @param accountCommand the command containing the data to create the underlying account.
 * @param entityId       the identifier of the entity to which the staff member belongs.
 */
public record StaffCreateCommand(
        CreateAccountCommand accountCommand, UUID entityId
) {
}
