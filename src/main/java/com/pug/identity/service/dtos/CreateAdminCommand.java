package com.pug.identity.service.dtos;

/**
 * Command to create a new admin user.
 *
 * @param accountCommand the command containing the data to create the underlying account.
 */
public record CreateAdminCommand(CreateAccountCommand accountCommand) {}
