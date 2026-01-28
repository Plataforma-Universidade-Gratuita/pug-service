package com.pug.identity.service.dtos;

import com.pug.shared.domain.enums.AccountType;

/**
 * Command DTO for creating a new Account.
 *
 * @param emailString the email address of the account as a string.
 * @param type the account type.
 * @param passwordHash the hashed password.
 * @param userCommand the command for the associated user.
 */
public record AccountCreateCommand(
    String emailString,
    AccountType type,
    String passwordHash,
    UserCreateCommand userCommand) {}
