package com.pug.identity.service.dtos;

import com.pug.identity.domain.vos.Email;

/**
 * Command to update an existing account's email and password.
 *
 * @param email the new email address for the account
 * @param passwordHash the new hashed password for the account
 * @param userCommand the command containing user update details
 */
public record UpdateAccountCommand(
    Email email, String passwordHash, CreateOrUpdateUserCommand userCommand) {}
