package br.org.catolicasc.pug.helpers.builders.commands;

import br.org.catolicasc.pug.helpers.TestBrazilianIdentifierGenerator;
import br.org.catolicasc.pug.helpers.TestNameGenerator;
import br.org.catolicasc.pug.identity.service.dtos.users.UserCreateCommand;

/**
 * Builder class for creating {@link UserCreateCommand} DTOs in test scenarios.
 *
 * <p>Provides a fluent API with random defaults, generating a valid CPF and unique name.
 */
public class UserCreateCommandBuilder {
  private String cpfString = TestBrazilianIdentifierGenerator.generateValidCpf();
  private String name = TestNameGenerator.generateRandomName();

  private UserCreateCommandBuilder() {}

  /**
   * Initializes a new builder with random defaults.
   *
   * @return a new {@link UserCreateCommandBuilder} instance
   */
  public static UserCreateCommandBuilder aUserCreateCommand() {
    return new UserCreateCommandBuilder();
  }

  /**
   * Sets the CPF string.
   *
   * @param cpfString the 11-digit CPF
   * @return this builder instance
   */
  public UserCreateCommandBuilder withCpf(String cpfString) {
    this.cpfString = cpfString;
    return this;
  }

  /**
   * Sets the person's name.
   *
   * @param name the full name
   * @return this builder instance
   */
  public UserCreateCommandBuilder withName(String name) {
    this.name = name;
    return this;
  }

  /**
   * Constructs the {@link UserCreateCommand} using the current builder state.
   *
   * @return a configured {@link UserCreateCommand} instance
   */
  public UserCreateCommand build() {
    return new UserCreateCommand(cpfString, name);
  }
}
