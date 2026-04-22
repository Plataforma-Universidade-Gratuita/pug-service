package br.org.catolicasc.pug.helpers.builders.commands;

import br.org.catolicasc.pug.helpers.TestBrazilianIdentifierGenerator;
import br.org.catolicasc.pug.helpers.TestNameGenerator;
import br.org.catolicasc.pug.identity.service.dtos.AccountCreateCommand;
import br.org.catolicasc.pug.identity.service.dtos.UserCreateCommand;
import br.org.catolicasc.pug.shared.domain.enums.AccountType;
import java.util.Random;

/**
 * Builder class for creating {@link AccountCreateCommand} DTOs in test scenarios.
 *
 * <p>Provides a fluent API with random defaults, composing a nested {@link UserCreateCommand}
 * internally. The user command is only included when CPF and name are provided.
 */
public class AccountCreateCommandBuilder {
  private String emailString = TestNameGenerator.generateUniqueEmail("pug.com");
  private AccountType type = getRandomAccountType();
  private String passwordHash = "hashed-password";
  private String userCpf = TestBrazilianIdentifierGenerator.generateValidCpf();
  private String userName = TestNameGenerator.generateRandomName();
  private boolean includeUser = true;

  private AccountCreateCommandBuilder() {}

  /**
   * Initializes a new builder with random defaults including a nested user command.
   *
   * @return a new {@link AccountCreateCommandBuilder} instance
   */
  public static AccountCreateCommandBuilder anAccountCreateCommand() {
    return new AccountCreateCommandBuilder();
  }

  /**
   * Sets the email address.
   *
   * @param emailString the email
   * @return this builder instance
   */
  public AccountCreateCommandBuilder withEmail(String emailString) {
    this.emailString = emailString;
    return this;
  }

  /**
   * Sets the account type.
   *
   * @param type the {@link AccountType}
   * @return this builder instance
   */
  public AccountCreateCommandBuilder withType(AccountType type) {
    this.type = type;
    return this;
  }

  /**
   * Sets the password hash.
   *
   * @param passwordHash the hashed password
   * @return this builder instance
   */
  public AccountCreateCommandBuilder withPasswordHash(String passwordHash) {
    this.passwordHash = passwordHash;
    return this;
  }

  /**
   * Sets the CPF for the nested user command.
   *
   * @param cpf the 11-digit CPF
   * @return this builder instance
   */
  public AccountCreateCommandBuilder withUserCpf(String cpf) {
    this.userCpf = cpf;
    return this;
  }

  /**
   * Sets the name for the nested user command.
   *
   * @param name the full name
   * @return this builder instance
   */
  public AccountCreateCommandBuilder withUserName(String name) {
    this.userName = name;
    return this;
  }

  /**
   * Excludes the nested user command (sets it to {@code null}).
   *
   * @return this builder instance
   */
  public AccountCreateCommandBuilder withoutUser() {
    this.includeUser = false;
    return this;
  }

  /**
   * Constructs the {@link AccountCreateCommand} using the current builder state.
   *
   * @return a configured {@link AccountCreateCommand} instance
   */
  public AccountCreateCommand build() {
    UserCreateCommand userCmd = includeUser ? new UserCreateCommand(userCpf, userName) : null;
    return new AccountCreateCommand(emailString, type, passwordHash, userCmd);
  }

  private static AccountType getRandomAccountType() {
    AccountType[] values = AccountType.values();
    return values[new Random().nextInt(values.length)];
  }
}
