package br.org.catolicasc.pug.identity.infra.read.impl;

import static org.assertj.core.api.Assertions.assertThat;

import br.org.catolicasc.pug.helpers.TestDataFactory;
import br.org.catolicasc.pug.identity.domain.Account;
import br.org.catolicasc.pug.identity.domain.Admin;
import br.org.catolicasc.pug.identity.domain.User;
import br.org.catolicasc.pug.shared.domain.enums.AccountType;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@QuarkusTest
class AdminQueriesImplTest {

  @Inject AdminQueriesImpl queries;
  @Inject TestDataFactory factory;

  private Admin admin;
  private Account account;

  @BeforeEach
  void setup() {
    User user = factory.createUser();
    account = factory.createAccount(user, AccountType.ADMIN);
    admin = factory.createAdmin(account); // Certifique-se de ter este método na factory
  }

  @Test
  @Transactional
  @DisplayName("Should project full nested AdminView for system admin")
  void shouldGetAdminView() {
    var adminView = queries.findOptionalById(account.getId());

    assertThat(adminView).isPresent();
    assertThat(adminView.get().campus()).isEqualTo(admin.getCampus());
    assertThat(adminView.get().accountView().email()).isEqualTo(account.getEmail().getValue());
  }

  @Test
  @Transactional
  @DisplayName("Should list system admin via AdminQueries")
  void shouldListAdmins() {
    var admins = queries.listAllAdmins();
    assertThat(admins).anyMatch(a -> a.accountView().id().equals(account.getId()));
  }
}
