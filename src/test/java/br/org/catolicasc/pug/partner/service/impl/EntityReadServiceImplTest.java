package br.org.catolicasc.pug.partner.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import br.org.catolicasc.pug.geo.infra.read.dtos.CityView;
import br.org.catolicasc.pug.geo.service.CityReadService;
import br.org.catolicasc.pug.helpers.TestBrazilianIdentifierGenerator;
import br.org.catolicasc.pug.partner.infra.read.EntityQueries;
import br.org.catolicasc.pug.partner.infra.read.dtos.EntityView;
import br.org.catolicasc.pug.shared.exceptions.ResourceNotFoundException;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@QuarkusTest
@DisplayName("EntityReadServiceImpl Coverage")
class EntityReadServiceImplTest {

  @Inject EntityReadServiceImpl service;
  @InjectMock EntityQueries queries;
  @InjectMock CityReadService cityReadService;

  @Test
  @DisplayName("Should return entity view by ID")
  void getByIdSuccess() {
    UUID id = UUID.randomUUID();
    String cnpj = TestBrazilianIdentifierGenerator.generateValidCnpj();
    EntityView view =
        new EntityView(
            id,
            cnpj,
            "WEG S.A.",
            "Addr",
            UUID.randomUUID(),
            OffsetDateTime.now(),
            OffsetDateTime.now());
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
  @DisplayName("Should list used cities via cityReadService")
  void listCityViews() {
    UUID cityId = UUID.randomUUID();
    when(queries.listAllCityIds()).thenReturn(List.of(cityId));
    when(cityReadService.listViewsByIds(List.of(cityId)))
        .thenReturn(List.of(new CityView(cityId, "Name", "123")));

    assertThat(service.listCityViews()).hasSize(1);
  }

  @Test
  @DisplayName("Should normalize search query and delegate")
  void search() {
    when(queries.searchByName("weg")).thenReturn(List.of());
    assertThat(service.searchViews("  WEG  ")).isEmpty();
  }

  @Test
  @DisplayName("Should retrieve EntityView by CNPJ successfully")
  void getViewByCnpjSuccess() {
    String cnpj = TestBrazilianIdentifierGenerator.generateValidCnpj();
    EntityView view =
        new EntityView(
            UUID.randomUUID(),
            cnpj,
            "WEG S.A.",
            "Addr",
            UUID.randomUUID(),
            OffsetDateTime.now(),
            OffsetDateTime.now());
    when(queries.findOptionalByCnpj(cnpj)).thenReturn(Optional.of(view));

    assertThat(service.getViewByCnpj(cnpj)).isEqualTo(view);
  }

  @Test
  @DisplayName("Should throw ResourceNotFound when CNPJ not found")
  void getViewByCnpjNotFound() {
    when(queries.findOptionalByCnpj("00000000000000")).thenReturn(Optional.empty());
    Assertions.assertThrows(
        ResourceNotFoundException.class, () -> service.getViewByCnpj("00000000000000"));
  }

  @Test
  @DisplayName("Should list all entity views")
  void listViews() {
    EntityView view =
        new EntityView(
            UUID.randomUUID(),
            TestBrazilianIdentifierGenerator.generateValidCnpj(),
            "WEG S.A.",
            "Addr",
            UUID.randomUUID(),
            OffsetDateTime.now(),
            OffsetDateTime.now());
    when(queries.listAllEntities()).thenReturn(List.of(view));

    List<EntityView> result = service.listViews();
    assertThat(result).hasSize(1);
    assertThat(result.getFirst()).isEqualTo(view);
  }

  @Test
  @DisplayName("Should list entity views by city ID")
  void listViewsByCityId() {
    UUID cityId = UUID.randomUUID();
    EntityView view =
        new EntityView(
            UUID.randomUUID(),
            TestBrazilianIdentifierGenerator.generateValidCnpj(),
            "WEG S.A.",
            "Addr",
            cityId,
            OffsetDateTime.now(),
            OffsetDateTime.now());
    when(queries.listAllByCityId(cityId)).thenReturn(List.of(view));

    List<EntityView> result = service.listViewsByCityId(cityId);
    assertThat(result).hasSize(1);
    assertThat(result.getFirst().cityId()).isEqualTo(cityId);
  }

  @Test
  @DisplayName("Should return empty list for null city ID")
  void listViewsByCityIdNull() {
    assertThat(service.listViewsByCityId(null)).isEmpty();
  }
}
