package br.org.catolicasc.pug.helpers.builders.commands;

import br.org.catolicasc.pug.helpers.TestNameGenerator;
import br.org.catolicasc.pug.identity.service.dtos.accounts.AccountUpdateCommand;
import br.org.catolicasc.pug.identity.service.dtos.users.UserUpdateCommand;
import br.org.catolicasc.pug.partner.service.dtos.staff.StaffUpdateCommand;
import com.github.f4b6a3.uuid.UuidCreator;
import java.util.UUID;

/**
 * Builder class for creating {@link StaffUpdateCommand} DTOs in test scenarios.
 *
 * <p>Provides a fluent API with sensible defaults, composing the nested {@link
 * AccountUpdateCommand} and {@link UserUpdateCommand} internally.
 */
public class StaffUpdateCommandBuilder {
  private String name = TestNameGenerator.generateRandomName();
  private String email = null;
  private UUID entityId = UuidCreator.getTimeOrderedEpoch();

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

  public StaffUpdateCommandBuilder withEntityId(UUID entityId) {
    this.entityId = entityId;
    return this;
  }

  public StaffUpdateCommand build() {
    UserUpdateCommand userCmd = (name != null) ? new UserUpdateCommand(name) : null;
    AccountUpdateCommand accCmd =
        (email != null || userCmd != null)
            ? new AccountUpdateCommand(email, null, null, userCmd)
            : null;
    return new StaffUpdateCommand(accCmd, entityId);
  }
}
