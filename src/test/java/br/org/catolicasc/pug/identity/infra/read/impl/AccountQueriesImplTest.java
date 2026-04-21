package br.org.catolicasc.pug.identity.infra.read.impl;

import static org.assertj.core.api.Assertions.assertThat;

import br.org.catolicasc.pug.helpers.BaseSearchTest;
import br.org.catolicasc.pug.helpers.TestDataFactory;
import br.org.catolicasc.pug.identity.domain.Account;
import br.org.catolicasc.pug.identity.domain.User;
import br.org.catolicasc.pug.identity.infra.persistence.UserEntity;
import br.org.catolicasc.pug.identity.infra.read.dtos.AccountView;
import br.org.catolicasc.pug.shared.domain.enums.AccountType;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

@QuarkusTest
@DisplayName("AccountQueriesImpl Coverage")
class AccountQueriesImplTest extends BaseSearchTest {

  @Inject AccountQueriesImpl queries;
  @Inject TestDataFactory factory;

  @ParameterizedTest
  @NullAndEmptySource
  @Transactional
  @DisplayName("Should return empty when email is null or empty")
  void findOptionalByEmailInvalid(String email) {
    assertThat(queries.findOptionalByEmail(email)).isEmpty();
  }

  @Test
  @Transactional
  @DisplayName("Should find account by email successfully")
  void findOptionalByEmailSuccess() {
    User user = factory.createUser();
    Account acc = factory.createAccount(user, AccountType.STUDENT);

    Optional<AccountView> found = queries.findOptionalByEmail(acc.getEmail().getValue());
    assertThat(found).isPresent();
    assertThat(found.get().email()).isEqualTo(acc.getEmail().getValue());
  }

  @Test
  @Transactional
  @DisplayName("Should return empty when ID is null")
  void findOptionalByIdNull() {
    assertThat(queries.findOptionalById(null)).isEmpty();
  }

  @Test
  @Transactional
  @DisplayName("Should find account by ID successfully")
  void findOptionalByIdSuccess() {
    User user = factory.createUser();
    Account acc = factory.createAccount(user, AccountType.STUDENT);

    Optional<AccountView> found = queries.findOptionalById(acc.getId());
    assertThat(found).isPresent();
    assertThat(found.get().id()).isEqualTo(acc.getId());
  }

  @Test
  @Transactional
  @DisplayName("Should list all accounts")
  void listAllAccounts() {
    factory.createAccount(factory.createUser(), AccountType.STUDENT);
    assertThat(queries.listAllAccounts()).isNotEmpty();
  }

  @ParameterizedTest
  @NullAndEmptySource
  @Transactional
  @DisplayName("Should return empty list when searching by CPF null or empty")
  void listByCpfInvalid(String cpf) {
    assertThat(queries.listByCpf(cpf)).isEmpty();
  }

  @Test
  @Transactional
  @DisplayName("Should list accounts by CPF successfully")
  void listByCpfSuccess() {
    User user = factory.createUser();
    factory.createAccount(user, AccountType.STUDENT);

    List<AccountView> found = queries.listByCpf(user.getCpf().getValue());
    assertThat(found).anyMatch(v -> v.userId().equals(user.getId()));
  }

  @Test
  @DisplayName("Should return empty list for non-matching search query")
  void searchByNameNotFound() {
    assertThat(queries.searchByName("NonExistent")).isEmpty();
  }

  @Test
  @DisplayName("Should search accounts by name successfully")
  void searchByNameSuccess() throws Exception {
    User[] u = new User[1];

    runInTransaction(
        () -> {
          u[0] = factory.createUser();
          factory.createAccount(u[0], AccountType.STUDENT);
        });

    syncIndex(UserEntity.class);

    String searchKey = u[0].getName().substring(0, 3);
    List<AccountView> found = queries.searchByName(searchKey);

    assertThat(found).anyMatch(v -> v.userId().equals(u[0].getId()));
  }
}
