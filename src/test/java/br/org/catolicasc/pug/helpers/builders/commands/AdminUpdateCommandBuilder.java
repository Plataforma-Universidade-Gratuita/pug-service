package br.org.catolicasc.pug.helpers.builders.commands;

import br.org.catolicasc.pug.identity.service.dtos.AccountUpdateCommand;
import br.org.catolicasc.pug.identity.service.dtos.AdminUpdateCommand;
import br.org.catolicasc.pug.identity.service.dtos.UserUpdateCommand;
import br.org.catolicasc.pug.shared.domain.enums.Campi;
import java.util.Random;

/** Builder class for creating {@link AdminUpdateCommand} DTOs in test scenarios. */
public class AdminUpdateCommandBuilder {
  private String name = null;
  private String email = null;
  private Campi campus = getRandomCampus();

  private AdminUpdateCommandBuilder() {}

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

  public AdminUpdateCommandBuilder withCampus(Campi campus) {
    this.campus = campus;
    return this;
  }

  public AdminUpdateCommand build() {
    UserUpdateCommand userCmd = name != null ? new UserUpdateCommand(name) : null;
    AccountUpdateCommand accCmd =
        (email != null || userCmd != null)
            ? new AccountUpdateCommand(email, null, null, userCmd)
            : null;
    return new AdminUpdateCommand(accCmd, campus);
  }

  private static Campi getRandomCampus() {
    Campi[] values = Campi.values();
    return values[new Random().nextInt(values.length)];
  }
}
