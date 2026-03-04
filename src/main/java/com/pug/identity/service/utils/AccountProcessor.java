package com.pug.identity.service.utils;

import com.pug.identity.domain.Account;
import com.pug.identity.domain.vos.Email;
import com.pug.shared.domain.enums.AccountType;
import com.pug.shared.utils.StringUtils;

import java.util.UUID;

/**
 * Stateless utility class responsible for mapping raw DTO command data into
 * pure {@link Account} Domain Aggregates and Value Objects.
 * <p>
 * This processor centralizes the orchestration of domain factory methods, enum parsing,
 * and state-mutation behaviors to keep the application service layer clean.
 */
public class AccountProcessor {

  /**
   * Processes raw creation inputs and constructs a new {@link Account} domain aggregate.
   * <p>
   * <b>Note:</b> The returned {@link Account} object may contain accumulated domain validation
   * failures. The caller is responsible for checking {@link Account#hasFieldErrors()} and
   * handling them appropriately.
   *
   * @param userId         the unique identifier of the user who owns this account
   * @param emailString    the raw email address string
   * @param accountTypeStr the raw string representing the account role (e.g., "ADMIN", "STUDENT")
   * @param passwordHash   the securely hashed password string
   * @return a fully instantiated {@link Account} domain aggregate, potentially containing validation errors
   */
  public static Account processCreateInput(
          UUID userId, String emailString, String accountTypeStr, String passwordHash) {

    Email emailVo = Email.factory(emailString);
    AccountType type =
            StringUtils.isEmpty(accountTypeStr) ? null : AccountType.valueOf(accountTypeStr);

    return Account.factory(userId, emailVo, type, passwordHash);
  }

  /**
   * Processes raw update inputs and conditionally mutates the state of an existing {@link Account}.
   * <p>
   * This method applies partial updates. Only fields that are explicitly provided
   * will trigger a state mutation via the aggregate's domain behaviors. Returns a new,
   * immutable instance reflecting the changes.
   *
   * @param existingAccount the current, reconstituted {@link Account} aggregate from the repository
   * @param emailString     the proposed new email address, or {@code null}/empty to skip updating
   * @param passwordHash    the proposed new password hash, or {@code null}/empty to skip updating
   * @return a new {@link Account} domain aggregate reflecting the requested updates, potentially containing validation errors
   */
  public static Account processUpdateInput(
          Account existingAccount, String emailString, String passwordHash) {

    Account updatedAccount = existingAccount;

    if (StringUtils.isNotEmpty(emailString)) {
      Email newEmail = Email.factory(emailString);
      updatedAccount = updatedAccount.changeEmail(newEmail);
    }

    if (StringUtils.isNotEmpty(passwordHash)) {
      updatedAccount = updatedAccount.changePasswordHash(passwordHash);
    }

    return updatedAccount;
  }
}