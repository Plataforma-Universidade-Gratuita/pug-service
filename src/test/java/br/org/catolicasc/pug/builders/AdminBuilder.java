package br.org.catolicasc.pug.builders;

import br.org.catolicasc.pug.identity.domain.Admin;
import br.org.catolicasc.pug.shared.domain.enums.Campi;
import java.util.UUID;

public class AdminBuilder {
  private UUID accountId = UUID.randomUUID();
  private Campi campus = Campi.JARAGUA_DO_SUL;

  private AdminBuilder() {}

  public static AdminBuilder anAdmin() {
    return new AdminBuilder();
  }

  public AdminBuilder forAccount(UUID accountId) {
    this.accountId = accountId;
    return this;
  }

  public AdminBuilder atCampus(Campi campus) {
    this.campus = campus;
    return this;
  }

  public Admin build() {
    return Admin.factory(accountId, campus);
  }
}
