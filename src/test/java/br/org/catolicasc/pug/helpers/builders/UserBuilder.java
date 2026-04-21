package br.org.catolicasc.pug.helpers.builders;

import br.org.catolicasc.pug.helpers.TestNameGenerator;
import br.org.catolicasc.pug.identity.domain.User;
import br.org.catolicasc.pug.identity.domain.vos.Cpf;
import java.util.Random;

/**
 * Builder class for creating {@link User} domain aggregates in test scenarios.
 *
 * <p>Provides a fluent API to define user properties, including CPF validation and random name
 * generation, to ensure unique and valid data for integration tests.
 */
public class UserBuilder {
  private String cpf = generateValidCpf();
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
   * Generates a valid random CPF according to standard Brazilian validation algorithms.
   *
   * @return a valid CPF string
   */
  private String generateValidCpf() {
    Random random = new Random();
    int[] cpf = new int[11];
    for (int i = 0; i < 9; i++) {
      cpf[i] = random.nextInt(10);
    }
    int sum = 0;
    for (int i = 0; i < 9; i++) {
      sum += cpf[i] * (10 - i);
    }
    cpf[9] = (sum % 11 < 2) ? 0 : 11 - (sum % 11);
    sum = 0;
    for (int i = 0; i < 10; i++) {
      sum += cpf[i] * (11 - i);
    }
    cpf[10] = (sum % 11 < 2) ? 0 : 11 - (sum % 11);

    StringBuilder sb = new StringBuilder();
    for (int digit : cpf) {
      sb.append(digit);
    }
    return sb.toString();
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
