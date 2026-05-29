package br.org.catolicasc.pug.helpers.builders.commands;

import br.org.catolicasc.pug.helpers.TestBrazilianIdentifierGenerator;
import br.org.catolicasc.pug.helpers.TestNameGenerator;
import br.org.catolicasc.pug.identity.service.dtos.AccountCreateCommand;
import br.org.catolicasc.pug.identity.service.dtos.UserCreateCommand;
import br.org.catolicasc.pug.partner.service.dtos.StaffCreateCommand;
import br.org.catolicasc.pug.shared.domain.enums.AccountType;
import com.github.f4b6a3.uuid.UuidCreator;
import java.util.UUID;

/**
 * Builder class for creating {@link StaffCreateCommand} DTOs in test scenarios.
 *
 * <p>Provides a fluent API with random defaults, composing the nested {@link AccountCreateCommand}
 * and {@link UserCreateCommand} internally.
 */
public class StaffCreateCommandBuilder {
  private UUID entityId = UuidCreator.getTimeOrderedEpoch();
  private String cpf = TestBrazilianIdentifierGenerator.generateValidCpf();
  private String name = TestNameGenerator.generateRandomName();
  private String email = TestNameGenerator.generateUniqueEmail("pug.com");
  private boolean includeUser = true;

  private StaffCreateCommandBuilder() {}

  public static StaffCreateCommandBuilder aStaffCreateCommand() {
    return new StaffCreateCommandBuilder();
  }

  public StaffCreateCommandBuilder withEntityId(UUID entityId) {
    this.entityId = entityId;
    return this;
  }

  public StaffCreateCommandBuilder withCpf(String cpf) {
    this.cpf = cpf;
    return this;
  }

  public StaffCreateCommandBuilder withName(String name) {
    this.name = name;
    return this;
  }

  public StaffCreateCommandBuilder withEmail(String email) {
    this.email = email;
    return this;
  }

  /**
   * Excludes the nested user command (sets it to {@code null}).
   *
   * @return this builder instance
   */
  public StaffCreateCommandBuilder withoutUser() {
    this.includeUser = false;
    return this;
  }

  public StaffCreateCommand build() {
    UserCreateCommand userCmd = includeUser ? new UserCreateCommand(cpf, name) : null;
    AccountCreateCommand accCmd =
        new AccountCreateCommand(email, AccountType.PARTNER, null, userCmd);
    return new StaffCreateCommand(entityId, accCmd);
  }
}
