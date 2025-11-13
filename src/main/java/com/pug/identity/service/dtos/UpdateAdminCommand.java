package com.pug.identity.service.dtos;

/**
 * Command to update an existing admin user.
 *
 * @param accountCommand the command containing the data to update the underlying account.
 */
public record UpdateAdminCommand(UpdateAccountCommand accountCommand) {}
