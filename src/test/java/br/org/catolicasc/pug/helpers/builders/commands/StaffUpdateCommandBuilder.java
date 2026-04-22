package br.org.catolicasc.pug.helpers.builders.commands;

import br.org.catolicasc.pug.helpers.TestNameGenerator;
import br.org.catolicasc.pug.identity.service.dtos.AccountUpdateCommand;
import br.org.catolicasc.pug.identity.service.dtos.UserUpdateCommand;
import br.org.catolicasc.pug.partner.service.dtos.StaffUpdateCommand;

/**
 * Builder class for creating {@link StaffUpdateCommand} DTOs in test scenarios.
 *
 * <p>Provides a fluent API with sensible defaults, composing the nested {@link
 * AccountUpdateCommand} and {@link UserUpdateCommand} internally.
 */
public class StaffUpdateCommandBuilder {
  private String name = TestNameGenerator.generateRandomName();
  private String email = null;
  private String password = null;

  private StaffUpdateCommandBuilder() {}

  public static StaffUpdateCommandBuilder aStaffUpdateCommand() {
    return new StaffUpdateCommandBuilder();
  }

  public StaffUpdateCommandBuilder withName(String name) {
    this.name = name;
    return this;
  }

  public StaffUpdateCommandBuilder withEmail(String email) {
    this.email = email;
    return this;
  }

  public StaffUpdateCommandBuilder withPassword(String password) {
    this.password = password;
    return this;
  }

  public StaffUpdateCommand build() {
    UserUpdateCommand userCmd = (name != null) ? new UserUpdateCommand(name) : null;
    AccountUpdateCommand accCmd =
        (email != null || password != null || userCmd != null)
            ? new AccountUpdateCommand(email, password, userCmd)
            : null;
    return new StaffUpdateCommand(accCmd);
  }
}
