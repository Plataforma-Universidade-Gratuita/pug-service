package br.org.catolicasc.pug.helpers.builders.commands;

import br.org.catolicasc.pug.helpers.TestNameGenerator;
import br.org.catolicasc.pug.identity.service.dtos.UserUpdateCommand;

/**
 * Builder class for creating {@link UserUpdateCommand} DTOs in test scenarios.
 *
 * <p>Provides a fluent API with a random default name.
 */
public class UserUpdateCommandBuilder {
  private String name = TestNameGenerator.generateRandomName();

  private UserUpdateCommandBuilder() {}

  /**
   * Initializes a new builder with random defaults.
   *
   * @return a new {@link UserUpdateCommandBuilder} instance
   */
  public static UserUpdateCommandBuilder aUserUpdateCommand() {
    return new UserUpdateCommandBuilder();
  }

  /**
   * Sets the person's name.
   *
   * @param name the new name, or {@code null} to leave unchanged
   * @return this builder instance
   */
  public UserUpdateCommandBuilder withName(String name) {
    this.name = name;
    return this;
  }

  /**
   * Constructs the {@link UserUpdateCommand} using the current builder state.
   *
   * @return a configured {@link UserUpdateCommand} instance
   */
  public UserUpdateCommand build() {
    return new UserUpdateCommand(name);
  }
}
