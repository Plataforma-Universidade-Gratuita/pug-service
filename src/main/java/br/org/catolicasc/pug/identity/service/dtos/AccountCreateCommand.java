package br.org.catolicasc.pug.identity.service.dtos;

import br.org.catolicasc.pug.identity.domain.Account;
import br.org.catolicasc.pug.identity.domain.User;
import br.org.catolicasc.pug.shared.domain.enums.AccountType;

/**
 * Data Transfer Object (DTO) acting as an application command to create a new authentication
 * Account.
 *
 * <p>This record encapsulates the raw input data required by the application service to instantiate
 * a new {@link Account} aggregate, alongside the nested command needed to provision its underlying
 * {@link User}.
 *
 * @param emailString the raw string representation of the requested email address
 * @param type the designated authorization role for the account
 * @param passwordHash the securely hashed password string, or {@code null} when the account should
 *     start without local credentials
 * @param userCommand the nested command containing the data for the associated user
 */
public record AccountCreateCommand(
    String emailString, AccountType type, String passwordHash, UserCreateCommand userCommand) {}
