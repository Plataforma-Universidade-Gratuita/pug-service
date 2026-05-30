package br.org.catolicasc.pug.identity.infra.read.impl;

import static org.assertj.core.api.Assertions.assertThat;

import br.org.catolicasc.pug.helpers.TestDataFactory;
import br.org.catolicasc.pug.identity.domain.Account;
import br.org.catolicasc.pug.identity.domain.Admin;
import br.org.catolicasc.pug.identity.domain.User;
import br.org.catolicasc.pug.identity.service.dtos.admins.AdminComplexSearchCriteria;
import br.org.catolicasc.pug.shared.domain.enums.AccountType;
import br.org.catolicasc.pug.shared.service.dtos.PageQuery;
import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@QuarkusTest
@TestTransaction
@DisplayName("AdminsQueriesImpl Coverage")
class AdminsQueriesImplTest {

  @Inject AdminsQueriesImpl queries;
  @Inject TestDataFactory factory;

  private Admin setupAdmin() {
    User user = factory.createUser();
    Account account = factory.createAccount(user, AccountType.ADMIN);
    return factory.createAdmin(account);
  }

  @Test
  @DisplayName("Should return empty when account ID is null")
  void findOptionalByIdNull() {
    assertThat(queries.findOptionalById(null)).isEmpty();
  }

  @Test
  @DisplayName("Should find admin by account ID successfully")
  void findOptionalByIdSuccess() {
    Admin admin = setupAdmin();
    Optional<?> found = queries.findOptionalById(admin.getAccountId());
    assertThat(found).isPresent();
  }

  @Test
  @DisplayName("Should list admins and filter by ids")
  void listOperations() {
    Admin admin = setupAdmin();
    assertThat(queries.listAllAdmins()).isNotEmpty();
    assertThat(queries.listAllByIds(List.of(admin.getAccountId()))).hasSize(1);
  }

  @Test
  @DisplayName("Should return empty list when ids filter is empty")
  void listAllByIdsEmpty() {
    assertThat(queries.listAllByIds(List.of())).isEmpty();
  }

  @Test
  @DisplayName("Should execute complex search successfully")
  void searchSuccess() {
    User user = factory.createUser();
    Account account = factory.createAccount(user, AccountType.ADMIN);
    factory.createAdmin(account);

    OffsetDateTime from = account.getAuditInfo().getCreatedAt().minusSeconds(1);
    OffsetDateTime to = account.getAuditInfo().getUpdatedAt().plusSeconds(1);

    var criteria =
        new AdminComplexSearchCriteria(
            user.getName().split(" ")[0],
            user.getCpf().getValue().substring(0, 3),
            account.getEmail().getValue().substring(0, 4),
            from,
            to,
            true);

    var result = queries.search(new PageQuery(0, 1), criteria);

    assertThat(result.content()).isNotEmpty();
    assertThat(result.content())
        .anySatisfy(
            view -> {
              assertThat(view.accountView().id()).isEqualTo(account.getId());
              assertThat(view.accountView().userId()).isEqualTo(user.getId());
            });
  }
}
