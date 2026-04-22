package br.org.catolicasc.pug.identity.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.org.catolicasc.pug.helpers.TestBrazilianIdentifierGenerator;
import br.org.catolicasc.pug.identity.infra.read.AdminQueries;
import br.org.catolicasc.pug.identity.infra.read.dtos.AdminView;
import br.org.catolicasc.pug.shared.domain.enums.Campi;
import br.org.catolicasc.pug.shared.exceptions.ResourceNotFoundException;
import com.github.f4b6a3.uuid.UuidCreator;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

@QuarkusTest
@DisplayName("AdminReadServiceImpl Coverage")
class AdminReadServiceImplTest {

  @Inject AdminReadServiceImpl service;
  @InjectMock AdminQueries queries;

  @Test
  @DisplayName("Should return admin view successfully")
  void getViewByAccountIdSuccess() {
    UUID id = UuidCreator.getTimeOrderedEpoch();
    AdminView view = new AdminView(null, null, Campi.JARAGUA_DO_SUL);
    when(queries.findOptionalById(id)).thenReturn(Optional.of(view));

    assertThat(service.getViewByAccountId(id)).isEqualTo(view);
  }

  @Test
  @DisplayName("Should throw ResourceNotFound when admin missing")
  void notFound() {
    when(queries.findOptionalById(any())).thenReturn(Optional.empty());
    assertThrows(
        ResourceNotFoundException.class,
        () -> service.getViewByAccountId(UuidCreator.getTimeOrderedEpoch()));
  }

  @Test
  @DisplayName("Should normalize search query")
  void searchNormalization() {
    service.search(" Joinville ");
    verify(queries).searchByName("joinville");
  }

  @Test
  @DisplayName("Should return admin view by email successfully")
  void getViewByEmailSuccess() {
    String email = "admin@pug.com";
    AdminView view = new AdminView(null, null, Campi.JARAGUA_DO_SUL);
    when(queries.findOptionalByEmail(email)).thenReturn(Optional.of(view));

    assertThat(service.getViewByEmail(email)).isEqualTo(view);
  }

  @Test
  @DisplayName("Should throw ResourceNotFound for invalid email lookup")
  void getViewByEmailNotFound() {
    when(queries.findOptionalByEmail("missing@pug.com")).thenReturn(Optional.empty());
    assertThrows(ResourceNotFoundException.class, () -> service.getViewByEmail("missing@pug.com"));
  }

  @ParameterizedTest
  @NullAndEmptySource
  @DisplayName("Should throw ResourceNotFound for null or empty email")
  void getViewByEmailInvalid(String email) {
    assertThrows(ResourceNotFoundException.class, () -> service.getViewByEmail(email));
  }

  @Test
  @DisplayName("Should list all admin views")
  void listViews() {
    when(queries.listAllAdmins())
        .thenReturn(List.of(new AdminView(null, null, Campi.JARAGUA_DO_SUL)));
    assertThat(service.listViews()).hasSize(1);
  }

  @Test
  @DisplayName("Should list admin views by CPF successfully")
  void listViewsByCpfSuccess() {
    String cpf = TestBrazilianIdentifierGenerator.generateValidCpf();
    when(queries.listByCpf(cpf))
        .thenReturn(List.of(new AdminView(null, null, Campi.JARAGUA_DO_SUL)));

    assertThat(service.listViewsByCpf(cpf)).hasSize(1);
  }

  @ParameterizedTest
  @NullAndEmptySource
  @DisplayName("Should return empty list for null or empty CPF list lookup")
  void listViewsByCpfInvalid(String cpf) {
    assertThat(service.listViewsByCpf(cpf)).isEmpty();
  }
}
