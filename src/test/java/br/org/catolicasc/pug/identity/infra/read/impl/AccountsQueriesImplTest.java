package br.org.catolicasc.pug.identity.infra.read.impl;

import static org.assertj.core.api.Assertions.assertThat;

import br.org.catolicasc.pug.helpers.BaseSearchTest;
import br.org.catolicasc.pug.helpers.TestDataFactory;
import br.org.catolicasc.pug.identity.domain.Account;
import br.org.catolicasc.pug.identity.domain.User;
import br.org.catolicasc.pug.identity.infra.read.dtos.AccountView;
import br.org.catolicasc.pug.identity.service.dtos.accounts.AccountComplexSearchCriteria;
import br.org.catolicasc.pug.shared.domain.enums.AccountType;
import br.org.catolicasc.pug.shared.service.dtos.PageQuery;
import com.github.f4b6a3.uuid.UuidCreator;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@QuarkusTest
@DisplayName("AccountsQueriesImpl Coverage")
class AccountsQueriesImplTest extends BaseSearchTest {

  @Inject AccountsQueriesImpl queries;
  @Inject TestDataFactory factory;

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
    Account account = factory.createAccount(user, AccountType.FORMER_STUDENT);

    Optional<AccountView> found = queries.findOptionalById(account.getId());
    assertThat(found).isPresent();
    assertThat(found.get().id()).isEqualTo(account.getId());
  }

  @Test
  @Transactional
  @DisplayName("Should list all accounts")
  void listAllAccounts() {
    factory.createAccount(factory.createUser(), AccountType.FORMER_STUDENT);
    assertThat(queries.listAllAccounts()).isNotEmpty();
  }

  @Test
  @DisplayName("Should return empty when listing accounts by invalid IDs")
  void listAllByIdsInvalid() {
    assertThat(queries.listAllByIds(null)).isEmpty();
    assertThat(queries.listAllByIds(List.of(UuidCreator.getTimeOrderedEpoch()))).isEmpty();
  }

  @Test
  @Transactional
  @DisplayName("Should list accounts by IDs successfully")
  void listAllByIdsSuccess() {
    User user = factory.createUser();
    Account account = factory.createAccount(user, AccountType.FORMER_STUDENT);

    List<AccountView> found = queries.listAllByIds(List.of(account.getId()));
    assertThat(found).hasSize(1);
    assertThat(found.getFirst().id()).isEqualTo(account.getId());
  }

  @Test
  @DisplayName("Should search accounts by complex criteria successfully")
  void searchSuccess() throws Exception {
    Account[] account = new Account[1];
    User[] user = new User[1];
    runInTransaction(
        () -> {
          user[0] = factory.createUser();
          account[0] = factory.createAccount(user[0], AccountType.FORMER_STUDENT);
        });

    var result =
        queries.search(
            new PageQuery(0, 10),
            new AccountComplexSearchCriteria(
                user[0].getName().split(" ")[0],
                user[0].getCpf().getValue().substring(0, 3),
                account[0].getEmail().getValue().substring(0, 4),
                List.of(account[0].getAccountType()),
                null,
                null,
                true));

    assertThat(result.content()).anyMatch(v -> v.id().equals(account[0].getId()));
    assertThat(result.page()).isEqualTo(0);
    assertThat(result.size()).isEqualTo(10);
  }

  @Test
  @DisplayName("Should search accounts by timestamp criteria successfully")
  void searchByTimestampSuccess() throws Exception {
    Account[] account = new Account[1];
    runInTransaction(
        () -> account[0] = factory.createAccount(factory.createUser(), AccountType.FORMER_STUDENT));

    OffsetDateTime createdAt = account[0].getAuditInfo().getCreatedAt();
    var result =
        queries.search(
            new PageQuery(0, 1),
            new AccountComplexSearchCriteria(
                null, null, null, null, createdAt.minusSeconds(1), createdAt.plusSeconds(1), true));

    assertThat(result.content()).anyMatch(v -> v.id().equals(account[0].getId()));
  }

  @Test
  @DisplayName("Should return paginated account list when search criteria is null")
  void searchWithoutCriteria() {
    var result = queries.search(new PageQuery(0, 10), null);
    assertThat(result.content()).hasSizeLessThanOrEqualTo(10);
  }
}

