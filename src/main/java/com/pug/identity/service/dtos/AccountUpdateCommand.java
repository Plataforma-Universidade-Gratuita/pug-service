package com.pug.identity.service.dtos;

/**
 * Command DTO for updating an existing Account.
 *
 * @param emailString the new email address as a string (optional).
 * @param passwordHash the new hashed password (optional).
 * @param userCommand the command for updating the associated user (optional).
 */
public record AccountUpdateCommand(
    String emailString, String passwordHash, UserUpdateCommand userCommand) {}
