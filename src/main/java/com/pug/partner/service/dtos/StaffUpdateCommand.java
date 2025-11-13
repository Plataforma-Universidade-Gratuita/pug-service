package com.pug.partner.service.dtos;

import com.pug.identity.service.dtos.UpdateAccountCommand;

/**
 * Command to update an existing staff member.
 *
 * @param accountCommand the command containing the data to update the underlying account.
 */
public record StaffUpdateCommand(
        UpdateAccountCommand accountCommand
) {
}
