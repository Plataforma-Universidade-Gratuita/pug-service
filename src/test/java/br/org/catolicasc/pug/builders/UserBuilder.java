package br.org.catolicasc.pug.builders;

import br.org.catolicasc.pug.identity.domain.User;
import br.org.catolicasc.pug.identity.domain.vos.Cpf;

public class UserBuilder {
  private String cpf = "11144477735";
  private String name = "Default User";

  private UserBuilder() {}

  public static UserBuilder aUser() {
    return new UserBuilder();
  }

  public UserBuilder withCpf(String cpf) {
    this.cpf = cpf;
    return this;
  }

  public UserBuilder withName(String name) {
    this.name = name;
    return this;
  }

  public User build() {
    return User.factory(Cpf.factory(cpf), name);
  }
}
