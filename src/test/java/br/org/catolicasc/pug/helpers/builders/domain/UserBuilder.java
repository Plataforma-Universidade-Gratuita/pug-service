package br.org.catolicasc.pug.helpers.builders.domain;

import br.org.catolicasc.pug.helpers.TestBrazilianIdentifierGenerator;
import br.org.catolicasc.pug.helpers.TestNameGenerator;
import br.org.catolicasc.pug.identity.domain.User;
import br.org.catolicasc.pug.identity.domain.vos.Cpf;

/**
 * Builder class for creating {@link User} domain aggregates in test scenarios.
 *
 * <p>Provides a fluent API to define user properties, including CPF validation and random name
 * generation, to ensure unique and valid data for integration tests.
 */
public class UserBuilder {
  private String cpf = TestBrazilianIdentifierGenerator.generateValidCpf();
  private String name = TestNameGenerator.generateRandomName();

  private UserBuilder() {}

  /**
   * Initializes a new instance of the UserBuilder.
   *
   * @return a new UserBuilder instance
   */
  public static UserBuilder aUser() {
    return new UserBuilder();
  }

  /**
   * Sets the CPF for the user.
   *
   * @param cpf the CPF string
   * @return this builder instance
   */
  public UserBuilder withCpf(String cpf) {
    this.cpf = cpf;
    return this;
  }

  /**
   * Sets the full name of the user.
   *
   * @param name the user's name
   * @return this builder instance
   */
  public UserBuilder withName(String name) {
    this.name = name;
    return this;
  }

  /**
   * Constructs the {@link User} aggregate using the current builder state.
   *
   * @return a configured {@link User} instance
   */
  public User build() {
    return User.factory(Cpf.factory(cpf), name);
  }
}
