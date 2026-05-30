package br.org.catolicasc.pug.identity.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import br.org.catolicasc.pug.identity.infra.read.AccountsQueries;
import br.org.catolicasc.pug.identity.infra.read.dtos.AccountComplexSearchView;
import br.org.catolicasc.pug.identity.infra.read.dtos.AccountView;
import br.org.catolicasc.pug.identity.service.dtos.accounts.AccountComplexSearchCriteria;
import br.org.catolicasc.pug.shared.domain.enums.AccountType;
import br.org.catolicasc.pug.shared.exceptions.ResourceNotFoundException;
import br.org.catolicasc.pug.shared.service.dtos.PageQuery;
import br.org.catolicasc.pug.shared.service.dtos.PageResult;
import com.github.f4b6a3.uuid.UuidCreator;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@QuarkusTest
@DisplayName("AccountsReadServiceImpl Coverage")
class AccountsReadServiceImplTest {

  @Inject AccountsReadServiceImpl service;
  @InjectMock AccountsQueries queries;

  @Test
  @DisplayName("Should return account view by ID")
  void getByIdSuccess() {
    UUID id = UuidCreator.getTimeOrderedEpoch();
    AccountView view =
        new AccountView(
            id,
            UuidCreator.getTimeOrderedEpoch(),
            "test@pug.com",
            AccountType.FORMER_STUDENT,
            OffsetDateTime.now(),
            OffsetDateTime.now(),
            true);
    when(queries.findOptionalById(id)).thenReturn(Optional.of(view));

    assertThat(service.getViewById(id)).isEqualTo(view);
  }

  @Test
  @DisplayName("Should throw exception when ID not found")
  void getByIdNotFound() {
    when(queries.findOptionalById(any())).thenReturn(Optional.empty());
    assertThrows(
        ResourceNotFoundException.class,
        () -> service.getViewById(UuidCreator.getTimeOrderedEpoch()));
  }

  @Test
  @DisplayName("Should list all account views")
  void listViews() {
    when(queries.listAllAccounts())
        .thenReturn(
            List.of(
                new AccountView(
                    UuidCreator.getTimeOrderedEpoch(),
                    UuidCreator.getTimeOrderedEpoch(),
                    "a@a.com",
                    AccountType.FORMER_STUDENT,
                    null,
                    null,
                    true)));
    assertThat(service.listViews()).hasSize(1);
  }

  @Test
  @DisplayName("Should return empty list for null IDs lookup")
  void listViewsByIdsInvalid() {
    assertThat(service.listViewsByIds(null)).isEmpty();
  }

  @Test
  @DisplayName("Should list account views by IDs successfully")
  void listViewsByIdsSuccess() {
    UUID id = UuidCreator.getTimeOrderedEpoch();
    when(queries.listAllByIds(List.of(id)))
        .thenReturn(
            List.of(
                new AccountView(
                    id,
                    UuidCreator.getTimeOrderedEpoch(),
                    "a@a.com",
                    AccountType.FORMER_STUDENT,
                    null,
                    null,
                    true)));

    assertThat(service.listViewsByIds(List.of(id))).hasSize(1);
  }

  @Test
  @DisplayName("Should search accounts by complex criteria")
  void search() {
    var criteria =
        new AccountComplexSearchCriteria(
            "Ana", null, null, List.of(AccountType.FORMER_STUDENT), null, null, true);
    var pageResult =
        new PageResult<>(
            List.of(
                new AccountComplexSearchView(
                    UuidCreator.getTimeOrderedEpoch(),
                    UuidCreator.getTimeOrderedEpoch(),
                    "Ana Silva",
                    "ana@pug.com",
                    AccountType.FORMER_STUDENT,
                    OffsetDateTime.now(),
                    OffsetDateTime.now(),
                    true)),
            0,
            10,
            1,
            1);

    when(queries.search(new PageQuery(0, 10), criteria)).thenReturn(pageResult);

    assertThat(service.search(new PageQuery(0, 10), criteria)).isEqualTo(pageResult);
  }
}

