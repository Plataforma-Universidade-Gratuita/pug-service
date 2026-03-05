package com.pug.identity.service.dtos;

import com.pug.shared.domain.enums.AccountType;

/**
 * Data Transfer Object (DTO) acting as an application command to create a new authentication
 * Account.
 *
 * <p>This record encapsulates the raw input data required by the application service to instantiate
 * a new {@link com.pug.identity.domain.Account} aggregate, alongside the nested command needed to
 * provision its underlying {@link com.pug.identity.domain.User}.
 *
 * @param emailString the raw string representation of the requested email address
 * @param type the designated authorization role for the account
 * @param passwordHash the securely hashed password string
 * @param userCommand the nested command containing the data for the associated user
 */
public record AccountCreateCommand(
    String emailString, AccountType type, String passwordHash, UserCreateCommand userCommand) {}
