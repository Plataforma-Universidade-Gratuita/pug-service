package br.org.catolicasc.pug.identity.infra.read.impl;

import static org.assertj.core.api.Assertions.assertThat;

import br.org.catolicasc.pug.helpers.TestDataFactory;
import br.org.catolicasc.pug.identity.domain.Account;
import br.org.catolicasc.pug.identity.domain.User;
import br.org.catolicasc.pug.shared.domain.enums.AccountType;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@QuarkusTest
class AccountQueriesImplTest {

  @Inject AccountQueriesImpl queries;
  @Inject TestDataFactory factory;

  private Account account;

  @BeforeEach
  void setup() {
    User user = factory.createUser();
    account = factory.createAccount(user, AccountType.STUDENT);
  }

  @Test
  @Transactional
  @DisplayName("Should project account data joined with user name")
  void shouldProjectView() {
    var view = queries.findOptionalByEmail(account.getEmail().getValue());

    assertThat(view).isPresent();
    assertThat(view.get().email()).isEqualTo(account.getEmail().getValue());
    assertThat(view.get().id()).isEqualTo(account.getId());
  }

  @Test
  @Transactional
  @DisplayName("Should list accounts sorted by user name")
  void shouldListSorted() {
    var list = queries.listAllAccounts();
    assertThat(list).isNotEmpty();
    assertThat(list).anyMatch(a -> a.id().equals(account.getId()));
  }
}
