package br.org.catolicasc.pug.helpers.builders.commands;

import br.org.catolicasc.pug.identity.service.dtos.AccountUpdateCommand;
import br.org.catolicasc.pug.identity.service.dtos.AdminUpdateCommand;
import br.org.catolicasc.pug.identity.service.dtos.UserUpdateCommand;
import br.org.catolicasc.pug.shared.domain.enums.Campi;
import java.util.Random;

/**
 * Builder class for creating {@link AdminUpdateCommand} DTOs in test scenarios.
 *
 * <p>Provides a fluent API with sensible defaults for partial updates, composing the nested {@link
 * AccountUpdateCommand} and {@link UserUpdateCommand} internally.
 */
public class AdminUpdateCommandBuilder {
  private String name = null;
  private String email = null;
  private String password = null;
  private Campi campus = getRandomCampus();

  private AdminUpdateCommandBuilder() {}

  /**
   * Initializes a new builder with sensible defaults for a partial update.
   *
   * @return a new {@link AdminUpdateCommandBuilder} instance
   */
  public static AdminUpdateCommandBuilder anAdminUpdateCommand() {
    return new AdminUpdateCommandBuilder();
  }

  public AdminUpdateCommandBuilder withName(String name) {
    this.name = name;
    return this;
  }

  public AdminUpdateCommandBuilder withEmail(String email) {
    this.email = email;
    return this;
  }

  public AdminUpdateCommandBuilder withPassword(String password) {
    this.password = password;
    return this;
  }

  /**
   * Sets the campus assignment.
   *
   * @param campus the new {@link Campi}, or {@code null} to leave unchanged
   * @return this builder instance
   */
  public AdminUpdateCommandBuilder withCampus(Campi campus) {
    this.campus = campus;
    return this;
  }

  /**
   * Constructs the {@link AdminUpdateCommand} using the current builder state.
   *
   * @return a configured {@link AdminUpdateCommand} instance
   */
  public AdminUpdateCommand build() {
    UserUpdateCommand userCmd = (name != null) ? new UserUpdateCommand(name) : null;
    AccountUpdateCommand accCmd =
        (email != null || password != null || userCmd != null)
            ? new AccountUpdateCommand(email, password, userCmd)
            : null;
    return new AdminUpdateCommand(accCmd, campus);
  }

  private static Campi getRandomCampus() {
    Campi[] values = Campi.values();
    return values[new Random().nextInt(values.length)];
  }
}
