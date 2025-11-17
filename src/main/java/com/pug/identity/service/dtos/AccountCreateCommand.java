package com.pug.identity.service.dtos;

import com.pug.identity.domain.vos.Email;
import com.pug.shared.domain.enums.AccountType;

/**
 * Command object for creating a new account.
 *
 * @param userCommand the command containing user details
 * @param email the email address for the account
 * @param type the type of the account
 * @param passwordHash the hashed password for the account
 */
public record AccountCreateCommand(
        UserCreateOrUpdateCommand userCommand, Email email, AccountType type, String passwordHash) {}
