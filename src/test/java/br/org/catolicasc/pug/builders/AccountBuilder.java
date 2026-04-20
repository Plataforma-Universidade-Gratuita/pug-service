package br.org.catolicasc.pug.builders;

import br.org.catolicasc.pug.identity.domain.Account;
import br.org.catolicasc.pug.identity.domain.vos.Email;
import br.org.catolicasc.pug.shared.domain.enums.AccountType;
import java.util.UUID;

public class AccountBuilder {
  private UUID userId = UUID.randomUUID();
  private String email = "test@pug.com";
  private AccountType type = AccountType.STUDENT;
  private String passwordHash = "hashed-password";

  private AccountBuilder() {}

  public static AccountBuilder anAccount() {
    return new AccountBuilder();
  }

  public AccountBuilder forUser(UUID userId) {
    this.userId = userId;
    return this;
  }

  public AccountBuilder withEmail(String email) {
    this.email = email;
    return this;
  }

  public AccountBuilder withType(AccountType type) {
    this.type = type;
    return this;
  }

  public Account build() {
    return Account.factory(userId, Email.factory(email), type, passwordHash);
  }
}
