package br.org.catolicasc.pug.identity.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import br.org.catolicasc.pug.helpers.TestBrazilianIdentifierGenerator;
import br.org.catolicasc.pug.identity.infra.read.UserQueries;
import br.org.catolicasc.pug.identity.infra.read.dtos.UserView;
import br.org.catolicasc.pug.shared.exceptions.ResourceNotFoundException;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@QuarkusTest
@DisplayName("UserReadServiceImpl Coverage")
class UserReadServiceImplTest {

  @Inject UserReadServiceImpl service;
  @InjectMock UserQueries queries;

  @Test
  @DisplayName("Should return user view by ID")
  void getByIdSuccess() {
    UUID id = UUID.randomUUID();
    String cpf = TestBrazilianIdentifierGenerator.generateValidCpf();
    UserView view = new UserView(id, cpf, "Test User", null, null);
    when(queries.findOptionalById(id)).thenReturn(Optional.of(view));

    assertThat(service.getViewById(id)).isEqualTo(view);
  }

  @Test
  @DisplayName("Should throw ResourceNotFound for unknown ID")
  void getByIdNotFound() {
    when(queries.findOptionalById(UUID.randomUUID())).thenReturn(Optional.empty());
    Assertions.assertThrows(
        ResourceNotFoundException.class, () -> service.getViewById(UUID.randomUUID()));
  }

  @Test
  @DisplayName("Should return user view by CPF")
  void getByCpfSuccess() {
    String cpf = TestBrazilianIdentifierGenerator.generateValidCpf();
    UserView view = new UserView(UUID.randomUUID(), cpf, "Test User", null, null);
    when(queries.findOptionalByCpf(cpf)).thenReturn(Optional.of(view));

    assertThat(service.getViewByCpf(cpf)).isEqualTo(view);
  }

  @Test
  @DisplayName("Should list all users")
  void listAll() {
    when(queries.listAllUsers())
        .thenReturn(List.of(new UserView(UUID.randomUUID(), "111", "User", null, null)));
    assertThat(service.listViews()).hasSize(1);
  }

  @Test
  @DisplayName("Should search users by name")
  void search() {
    when(queries.searchByName("ana"))
        .thenReturn(List.of(new UserView(UUID.randomUUID(), "111", "Ana", null, null)));
    assertThat(service.search("  Ana  ")).isNotEmpty();
  }
}
