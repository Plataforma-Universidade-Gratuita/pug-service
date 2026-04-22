package br.org.catolicasc.pug.partner.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import br.org.catolicasc.pug.helpers.TestBrazilianIdentifierGenerator;
import br.org.catolicasc.pug.partner.infra.read.StaffQueries;
import br.org.catolicasc.pug.partner.infra.read.dtos.StaffView;
import br.org.catolicasc.pug.shared.exceptions.ResourceNotFoundException;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@QuarkusTest
@DisplayName("StaffReadServiceImpl Coverage")
class StaffReadServiceImplTest {

  @Inject StaffReadServiceImpl service;
  @InjectMock StaffQueries queries;

  @Test
  @DisplayName("Should return staff view by account ID")
  void getByAccountIdSuccess() {
    UUID accountId = UUID.randomUUID();
    StaffView view = new StaffView(null, UUID.randomUUID(), UUID.randomUUID());
    when(queries.findOptionalById(accountId)).thenReturn(Optional.of(view));

    assertThat(service.getViewByAccountId(accountId)).isEqualTo(view);
  }

  @Test
  @DisplayName("Should throw ResourceNotFound when staff not found")
  void getByAccountIdNotFound() {
    when(queries.findOptionalById(any())).thenReturn(Optional.empty());
    assertThrows(
        ResourceNotFoundException.class, () -> service.getViewByAccountId(UUID.randomUUID()));
  }

  @Test
  @DisplayName("Should list staff views by entity ID")
  void listByEntityId() {
    UUID entityId = UUID.randomUUID();
    when(queries.listAllByEntityId(entityId))
        .thenReturn(List.of(new StaffView(null, entityId, UUID.randomUUID())));

    assertThat(service.listViewsByEntityId(entityId)).hasSize(1);
  }

  @Test
  @DisplayName("Should fold search term and search by name")
  void search() {
    when(queries.searchByName("joao")).thenReturn(List.of());
    assertThat(service.search("  João  ")).isEmpty();
  }

  @Test
  @DisplayName("Should list all staff views")
  void listViews() {
    StaffView view = new StaffView(null, UUID.randomUUID(), UUID.randomUUID());
    when(queries.listAllStaff()).thenReturn(List.of(view));

    List<StaffView> result = service.listViews();
    assertThat(result).hasSize(1);
    assertThat(result.getFirst()).isEqualTo(view);
  }

  @Test
  @DisplayName("Should list staff views by CPF successfully")
  void listViewsByCpfSuccess() {
    String cpf = TestBrazilianIdentifierGenerator.generateValidCpf();
    StaffView view = new StaffView(null, UUID.randomUUID(), UUID.randomUUID());
    when(queries.listByCpf(cpf)).thenReturn(List.of(view));

    List<StaffView> result = service.listViewsByCpf(cpf);
    assertThat(result).hasSize(1);
    assertThat(result.getFirst()).isEqualTo(view);
  }

  @Test
  @DisplayName("Should return empty list for null or empty CPF")
  void listViewsByCpfInvalid() {
    assertThat(service.listViewsByCpf(null)).isEmpty();
    assertThat(service.listViewsByCpf("")).isEmpty();
  }

  @Test
  @DisplayName("Should return staff view by email successfully")
  void getViewByEmailSuccess() {
    String email = "test@pug.com";
    StaffView view = new StaffView(null, UUID.randomUUID(), UUID.randomUUID());
    when(queries.findOptionalByEmail(email)).thenReturn(Optional.of(view));

    assertThat(service.getViewByEmail(email)).isEqualTo(view);
  }

  @Test
  @DisplayName("Should throw ResourceNotFound when email not found")
  void getViewByEmailNotFound() {
    when(queries.findOptionalByEmail("missing@pug.com")).thenReturn(Optional.empty());
    assertThrows(ResourceNotFoundException.class, () -> service.getViewByEmail("missing@pug.com"));
  }
}
