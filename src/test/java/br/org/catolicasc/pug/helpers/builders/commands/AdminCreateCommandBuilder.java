package br.org.catolicasc.pug.helpers.builders.commands;

import br.org.catolicasc.pug.helpers.TestBrazilianIdentifierGenerator;
import br.org.catolicasc.pug.helpers.TestNameGenerator;
import br.org.catolicasc.pug.identity.service.dtos.AccountCreateCommand;
import br.org.catolicasc.pug.identity.service.dtos.AdminCreateCommand;
import br.org.catolicasc.pug.identity.service.dtos.UserCreateCommand;
import br.org.catolicasc.pug.shared.domain.enums.AccountType;
import br.org.catolicasc.pug.shared.domain.enums.Campi;
import java.util.Random;

/**
 * Builder class for creating {@link AdminCreateCommand} DTOs in test scenarios.
 *
 * <p>Provides a fluent API with random defaults, composing the nested {@link AccountCreateCommand}
 * and {@link UserCreateCommand} internally.
 */
public class AdminCreateCommandBuilder {
  private String cpf = TestBrazilianIdentifierGenerator.generateValidCpf();
  private String name = TestNameGenerator.generateRandomName();
  private String email = TestNameGenerator.generateUniqueEmail("pug.com");
  private String password = "hashed-password";
  private Campi campus = getRandomCampus();

  private AdminCreateCommandBuilder() {}

  /**
   * Initializes a new builder with random defaults.
   *
   * @return a new {@link AdminCreateCommandBuilder} instance
   */
  public static AdminCreateCommandBuilder anAdminCreateCommand() {
    return new AdminCreateCommandBuilder();
  }

  public AdminCreateCommandBuilder withCpf(String cpf) {
    this.cpf = cpf;
    return this;
  }

  public AdminCreateCommandBuilder withName(String name) {
    this.name = name;
    return this;
  }

  public AdminCreateCommandBuilder withEmail(String email) {
    this.email = email;
    return this;
  }

  public AdminCreateCommandBuilder withPassword(String password) {
    this.password = password;
    return this;
  }

  /**
   * Sets the campus assignment.
   *
   * @param campus the {@link Campi} enum value
   * @return this builder instance
   */
  public AdminCreateCommandBuilder withCampus(Campi campus) {
    this.campus = campus;
    return this;
  }

  /**
   * Constructs the {@link AdminCreateCommand} using the current builder state, composing the nested
   * {@link AccountCreateCommand} and {@link UserCreateCommand} internally.
   *
   * @return a configured {@link AdminCreateCommand} instance
   */
  public AdminCreateCommand build() {
    UserCreateCommand userCmd = new UserCreateCommand(cpf, name);
    AccountCreateCommand accCmd =
        new AccountCreateCommand(email, AccountType.ADMIN, password, userCmd);
    return new AdminCreateCommand(accCmd, campus);
  }

  private static Campi getRandomCampus() {
    Campi[] values = Campi.values();
    return values[new Random().nextInt(values.length)];
  }
}
