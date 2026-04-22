package br.org.catolicasc.pug.geo.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import br.org.catolicasc.pug.geo.infra.read.CityQueries;
import br.org.catolicasc.pug.geo.infra.read.dtos.CityView;
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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

@QuarkusTest
@DisplayName("CityReadServiceImpl Coverage")
class CityReadServiceImplTest {

  @Inject CityReadServiceImpl service;
  @InjectMock CityQueries queries;

  @Test
  @DisplayName("Should return city view by ID")
  void getByIdSuccess() {
    UUID id = UUID.randomUUID();
    CityView view = new CityView(id, "Joinville", "4209106");
    when(queries.findOptionalById(id)).thenReturn(Optional.of(view));

    assertThat(service.getViewById(id)).isEqualTo(view);
  }

  @Test
  @DisplayName("Should throw ResourceNotFound when ID not found")
  void getByIdNotFound() {
    when(queries.findOptionalById(any())).thenReturn(Optional.empty());
    Assertions.assertThrows(
        ResourceNotFoundException.class, () -> service.getViewById(UUID.randomUUID()));
  }

  @Test
  @DisplayName("Should return city view by IBGE code")
  void getViewByIbgeSuccess() {
    String ibge = "4209106";
    CityView view = new CityView(UUID.randomUUID(), "Joinville", ibge);
    when(queries.findOptionalByIbgeCode(ibge)).thenReturn(Optional.of(view));

    assertThat(service.getViewByIbgeCode(ibge)).isEqualTo(view);
  }

  @ParameterizedTest
  @NullAndEmptySource
  @DisplayName("Should throw ResourceNotFound for invalid IBGE code")
  void getViewByIbgeInvalid(String ibge) {
    Assertions.assertThrows(ResourceNotFoundException.class, () -> service.getViewByIbgeCode(ibge));
  }

  @Test
  @DisplayName("Should list all city views")
  void listViews() {
    when(queries.listAllCities())
        .thenReturn(List.of(new CityView(UUID.randomUUID(), "Joinville", "4209106")));
    assertThat(service.listViews()).hasSize(1);
  }

  @Test
  @DisplayName("Should search cities by normalized query")
  void search() {
    when(queries.searchByName("joinville")).thenReturn(List.of());
    assertThat(service.search("  Joinville  ")).isEmpty();
  }

  @Test
  @DisplayName("Should list city views by IDs successfully")
  void listViewsByIdsSuccess() {
    UUID id = UUID.randomUUID();
    CityView view = new CityView(id, "Joinville", "4209106");
    when(queries.listAllByIds(List.of(id))).thenReturn(List.of(view));

    List<CityView> result = service.listViewsByIds(List.of(id));

    assertThat(result).hasSize(1);
    assertThat(result.getFirst().id()).isEqualTo(id);
  }

  @Test
  @DisplayName("Should return empty list when provided ID list is null or empty")
  void listViewsByIdsInvalid() {
    assertThat(service.listViewsByIds(null)).isEmpty();
    assertThat(service.listViewsByIds(List.of())).isEmpty();
  }
}
