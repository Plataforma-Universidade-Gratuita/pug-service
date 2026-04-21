package br.org.catolicasc.pug.identity.infra.read.impl;

import static org.assertj.core.api.Assertions.assertThat;

import br.org.catolicasc.pug.helpers.TestDataFactory;
import br.org.catolicasc.pug.identity.domain.Account;
import br.org.catolicasc.pug.identity.domain.Admin;
import br.org.catolicasc.pug.identity.domain.User;
import br.org.catolicasc.pug.identity.infra.read.dtos.AdminView;
import br.org.catolicasc.pug.shared.domain.enums.AccountType;
import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

@QuarkusTest
@TestTransaction
@DisplayName("AdminQueriesImpl Coverage")
class AdminQueriesImplTest {

  @Inject AdminQueriesImpl queries;
  @Inject TestDataFactory factory;

  private Admin setupAdmin() {
    User user = factory.createUser();
    Account account = factory.createAccount(user, AccountType.ADMIN);
    return factory.createAdmin(account);
  }

  @ParameterizedTest
  @NullAndEmptySource
  @DisplayName("Should return empty when email is null or empty")
  void findOptionalByEmailInvalid(String email) {
    assertThat(queries.findOptionalByEmail(email)).isEmpty();
  }

  @Test
  @DisplayName("Should find admin by email successfully")
  void findOptionalByEmailSuccess() {
    Admin admin = setupAdmin();
    Optional<AdminView> found = queries.findOptionalByEmail("admin@pug.com");
    assertThat(found).isPresent();
  }

  @Test
  @DisplayName("Should return empty when account ID is null")
  void findOptionalByIdNull() {
    assertThat(queries.findOptionalById(null)).isEmpty();
  }

  @Test
  @DisplayName("Should list admins and search by name")
  void listAndSearchOperations() {
    setupAdmin();
    assertThat(queries.listAllAdmins()).isNotEmpty();
    assertThat(queries.searchByName("System")).isNotEmpty();
  }

  @ParameterizedTest
  @NullAndEmptySource
  @DisplayName("Should return empty list when searching by CPF null or empty")
  void listByCpfInvalid(String cpf) {
    assertThat(queries.listByCpf(cpf)).isEmpty();
  }
}
