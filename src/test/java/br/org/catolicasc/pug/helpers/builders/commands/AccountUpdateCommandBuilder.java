package br.org.catolicasc.pug.helpers.builders.commands;

import br.org.catolicasc.pug.helpers.TestNameGenerator;
import br.org.catolicasc.pug.identity.service.dtos.AccountUpdateCommand;
import br.org.catolicasc.pug.identity.service.dtos.UserUpdateCommand;

/**
 * Builder class for creating {@link AccountUpdateCommand} DTOs in test scenarios.
 *
 * <p>Provides a fluent API with sensible defaults for partial updates, composing a nested {@link
 * UserUpdateCommand} internally when a name is provided.
 */
public class AccountUpdateCommandBuilder {
  private String emailString = TestNameGenerator.generateUniqueEmail("pug.com");
  private String passwordHash = null;
  private String userName = TestNameGenerator.generateRandomName();

  private AccountUpdateCommandBuilder() {}

  /**
   * Initializes a new builder with sensible defaults.
   *
   * @return a new {@link AccountUpdateCommandBuilder} instance
   */
  public static AccountUpdateCommandBuilder anAccountUpdateCommand() {
    return new AccountUpdateCommandBuilder();
  }

  /**
   * Sets the email address.
   *
   * @param emailString the new email, or {@code null} to leave unchanged
   * @return this builder instance
   */
  public AccountUpdateCommandBuilder withEmail(String emailString) {
    this.emailString = emailString;
    return this;
  }

  /**
   * Sets the password hash.
   *
   * @param passwordHash the new password hash, or {@code null} to leave unchanged
   * @return this builder instance
   */
  public AccountUpdateCommandBuilder withPasswordHash(String passwordHash) {
    this.passwordHash = passwordHash;
    return this;
  }

  /**
   * Sets the name for the nested user update command.
   *
   * @param name the new name, or {@code null} to skip the user update
   * @return this builder instance
   */
  public AccountUpdateCommandBuilder withUserName(String name) {
    this.userName = name;
    return this;
  }

  /**
   * Constructs the {@link AccountUpdateCommand} using the current builder state.
   *
   * @return a configured {@link AccountUpdateCommand} instance
   */
  public AccountUpdateCommand build() {
    UserUpdateCommand userCmd = (userName != null) ? new UserUpdateCommand(userName) : null;
    return new AccountUpdateCommand(emailString, passwordHash, userCmd);
  }
}
