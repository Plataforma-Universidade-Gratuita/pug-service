package br.org.catolicasc.pug.identity.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import br.org.catolicasc.pug.helpers.TestBrazilianIdentifierGenerator;
import br.org.catolicasc.pug.identity.infra.read.AccountQueries;
import br.org.catolicasc.pug.identity.infra.read.dtos.AccountView;
import br.org.catolicasc.pug.shared.domain.enums.AccountType;
import br.org.catolicasc.pug.shared.exceptions.ResourceNotFoundException;
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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

@QuarkusTest
@DisplayName("AccountReadServiceImpl Coverage")
class AccountReadServiceImplTest {

  @Inject AccountReadServiceImpl service;
  @InjectMock AccountQueries queries;

  @Test
  @DisplayName("Should return account view by ID")
  void getByIdSuccess() {
    UUID id = UuidCreator.getTimeOrderedEpoch();
    AccountView view =
        new AccountView(
            id,
            UuidCreator.getTimeOrderedEpoch(),
            "test@pug.com",
            AccountType.STUDENT,
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
  @DisplayName("Should search accounts by query")
  void search() {
    when(queries.searchByName("test")).thenReturn(List.of());
    assertThat(service.search("  Test  ")).isEmpty();
  }

  @Test
  @DisplayName("Should return account view by email successfully")
  void getViewByEmailSuccess() {
    String email = "test@pug.com";
    AccountView view =
        new AccountView(
            UuidCreator.getTimeOrderedEpoch(),
            UuidCreator.getTimeOrderedEpoch(),
            email,
            AccountType.STUDENT,
            null,
            null,
            true);
    when(queries.findOptionalByEmail(email)).thenReturn(Optional.of(view));

    assertThat(service.getViewByEmail(email)).isEqualTo(view);
  }

  @ParameterizedTest
  @NullAndEmptySource
  @DisplayName("Should throw ResourceNotFound for null/empty email")
  void getViewByEmailInvalid(String email) {
    assertThrows(ResourceNotFoundException.class, () -> service.getViewByEmail(email));
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
                    AccountType.STUDENT,
                    null,
                    null,
                    true)));
    assertThat(service.listViews()).hasSize(1);
  }

  @ParameterizedTest
  @NullAndEmptySource
  @DisplayName("Should return empty list for null/empty CPF list lookup")
  void listViewsByCpfInvalid(String cpf) {
    assertThat(service.listViewsByCpf(cpf)).isEmpty();
  }

  @Test
  @DisplayName("Should list account views by CPF successfully")
  void listViewsByCpfSuccess() {
    String cpf = TestBrazilianIdentifierGenerator.generateValidCpf();
    when(queries.listByCpf(cpf))
        .thenReturn(
            List.of(
                new AccountView(
                    UuidCreator.getTimeOrderedEpoch(),
                    UuidCreator.getTimeOrderedEpoch(),
                    "a@a.com",
                    AccountType.STUDENT,
                    null,
                    null,
                    true)));

    assertThat(service.listViewsByCpf(cpf)).hasSize(1);
  }
}
