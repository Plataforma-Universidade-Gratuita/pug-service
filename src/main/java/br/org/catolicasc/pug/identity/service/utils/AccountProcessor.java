package br.org.catolicasc.pug.identity.service.utils;

import br.org.catolicasc.pug.identity.domain.Account;
import br.org.catolicasc.pug.identity.domain.vos.Cpf;
import br.org.catolicasc.pug.identity.domain.vos.Email;
import br.org.catolicasc.pug.identity.service.dtos.accounts.AccountCreateCommand;
import br.org.catolicasc.pug.identity.service.dtos.users.UserCreateCommand;
import br.org.catolicasc.pug.shared.domain.enums.AccountType;
import br.org.catolicasc.pug.shared.exceptions.AppValidationException;
import br.org.catolicasc.pug.shared.utils.StringUtils;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Stateless utility class responsible for mapping raw DTO command data into pure {@link Account}
 * Domain Aggregates and Value Objects.
 *
 * <p>This processor centralizes the orchestration of domain factory methods, enum parsing, and
 * state-mutation behaviors to keep the application service layer clean. It also handles complex
 * list transformations for bulk processing.
 */
public class AccountProcessor {

  /**
   * Extracts a distinct list of user creation commands for individuals who do not yet exist in the
   * system.
   *
   * @param cmds the {@link List} of bulk account creation commands
   * @param existingUserMap a {@link Map} of currently existing users (CPF mapped to User UUID)
   * @return a distinct {@link List} of {@link UserCreateCommand} representing the missing users
   */
  public static List<UserCreateCommand> extractMissingUserCommands(
      List<AccountCreateCommand> cmds, Map<String, UUID> existingUserMap) {

    Map<String, UserCreateCommand> missing = new LinkedHashMap<>();

    for (AccountCreateCommand cmd : cmds) {
      UserCreateCommand userCmd = cmd.userCommand();
      Cpf cpf = Cpf.factory(userCmd.cpfString());
      String cpfVal = cpf.getValue();

      if (cpfVal != null && !existingUserMap.containsKey(cpfVal)) {
        missing.putIfAbsent(cpfVal, userCmd);
      }
    }

    return new ArrayList<>(missing.values());
  }

  /**
   * Processes a bulk list of account creation commands, generating a list of pure Domain
   * Aggregates.
   *
   * <p>This method maps each command to its underlying user identifier and triggers the aggregate's
   * internal validations. If any account violates domain rules, an exception is thrown to abort the
   * entire transaction.
   *
   * @param cmds the {@link List} of bulk account creation commands
   * @param completeUserMap a fully populated {@link Map} resolving CPFs to User UUIDs
   * @return a {@link List} of instantiated and validated {@link Account} aggregates
   * @throws AppValidationException if any aggregate contains domain validation errors
   */
  public static List<Account> processBulkCreateInput(
      List<AccountCreateCommand> cmds, Map<String, UUID> completeUserMap) {

    List<Account> accounts = new ArrayList<>(cmds.size());

    for (AccountCreateCommand cmd : cmds) {
      String cleanCpf = Cpf.factory(cmd.userCommand().cpfString()).getValue();
      UUID mappedUserId = completeUserMap.get(cleanCpf);
      String typeString = cmd.type() != null ? cmd.type().name() : null;

      Account account =
          processCreateInput(mappedUserId, cmd.emailString(), typeString, cmd.passwordHash());

      if (account.hasFieldErrors()) {
        throw new AppValidationException(account.getFieldErrors());
      }
      accounts.add(account);
    }

    return accounts;
  }

  /**
   * Processes raw creation inputs and constructs a new {@link Account} domain aggregate.
   *
   * <p><b>Note:</b> The returned {@link Account} object may contain accumulated domain validation
   * failures. The caller is responsible for checking {@link Account#hasFieldErrors()} and handling
   * them appropriately.
   *
   * @param userId the unique identifier of the user who owns this account
   * @param emailString the raw email address string
   * @param accountTypeStr the raw string representing the account role (e.g., "ADMIN",
   *     "FORMER_STUDENT")
   * @param passwordHash the securely hashed password string
   * @return a fully instantiated {@link Account} domain aggregate, potentially containing
   *     validation errors
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
   *
   * <p>This method applies partial updates. Only fields that are explicitly provided will trigger a
   * state mutation via the aggregate's domain behaviors. Returns a new, immutable instance
   * reflecting the changes.
   *
   * @param existingAccount the current, reconstituted {@link Account} aggregate from the repository
   * @param emailString the proposed new email address, or {@code null}/empty to skip updating
   * @param passwordHash the proposed new password hash, or {@code null}/empty to skip updating
   * @param active the proposed activation flag, or {@code null} to keep the current account status
   * @return a new {@link Account} domain aggregate reflecting the requested updates, potentially
   *     containing validation errors
   */
  public static Account processUpdateInput(
      Account existingAccount, String emailString, String passwordHash, Boolean active) {

    Account updatedAccount = existingAccount;

    if (StringUtils.isNotEmpty(emailString)) {
      Email newEmail = Email.factory(emailString);
      updatedAccount = updatedAccount.changeEmail(newEmail);
    }

    if (StringUtils.isNotEmpty(passwordHash)) {
      updatedAccount = updatedAccount.changePasswordHash(passwordHash);
    }

    if (active != null) {
      updatedAccount = active ? updatedAccount.activate() : updatedAccount.deactivate();
    }

    return updatedAccount;
  }
}
