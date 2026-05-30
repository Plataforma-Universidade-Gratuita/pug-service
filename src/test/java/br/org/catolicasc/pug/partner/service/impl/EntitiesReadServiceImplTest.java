package br.org.catolicasc.pug.partner.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import br.org.catolicasc.pug.helpers.TestBrazilianIdentifierGenerator;
import br.org.catolicasc.pug.partner.infra.read.EntitiesQueries;
import br.org.catolicasc.pug.partner.infra.read.dtos.EntityComplexSearchView;
import br.org.catolicasc.pug.partner.infra.read.dtos.EntityView;
import br.org.catolicasc.pug.partner.service.dtos.entities.EntityComplexSearchCriteria;
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
@DisplayName("EntitiesReadServiceImpl Coverage")
class EntitiesReadServiceImplTest {

  @Inject EntitiesReadServiceImpl service;
  @InjectMock EntitiesQueries queries;

  @Test
  @DisplayName("Should return entity view by ID")
  void getByIdSuccess() {
    UUID id = UuidCreator.getTimeOrderedEpoch();
    String cnpj = TestBrazilianIdentifierGenerator.generateValidCnpj();
    EntityView view =
        new EntityView(
            id,
            cnpj,
            "WEG S.A.",
            "Addr",
            UuidCreator.getTimeOrderedEpoch(),
            OffsetDateTime.now(),
            OffsetDateTime.now());
    when(queries.findOptionalById(id)).thenReturn(Optional.of(view));

    assertThat(service.getViewById(id)).isEqualTo(view);
  }

  @Test
  @DisplayName("Should throw ResourceNotFound when ID not found")
  void getByIdNotFound() {
    when(queries.findOptionalById(any())).thenReturn(Optional.empty());
    assertThrows(
        ResourceNotFoundException.class,
        () -> service.getViewById(UuidCreator.getTimeOrderedEpoch()));
  }

  @Test
  @DisplayName("Should list all entity views")
  void listViews() {
    EntityView view =
        new EntityView(
            UuidCreator.getTimeOrderedEpoch(),
            TestBrazilianIdentifierGenerator.generateValidCnpj(),
            "WEG S.A.",
            "Addr",
            UuidCreator.getTimeOrderedEpoch(),
            OffsetDateTime.now(),
            OffsetDateTime.now());
    when(queries.listAllEntities()).thenReturn(List.of(view));

    List<EntityView> result = service.listViews();
    assertThat(result).hasSize(1);
    assertThat(result.getFirst()).isEqualTo(view);
  }

  @Test
  @DisplayName("Should list entity views by IDs")
  void listViewsByIds() {
    UUID id = UuidCreator.getTimeOrderedEpoch();
    EntityView view =
        new EntityView(
            id,
            TestBrazilianIdentifierGenerator.generateValidCnpj(),
            "WEG S.A.",
            "Addr",
            UuidCreator.getTimeOrderedEpoch(),
            OffsetDateTime.now(),
            OffsetDateTime.now());
    when(queries.listAllByIds(List.of(id))).thenReturn(List.of(view));

    List<EntityView> result = service.listViewsByIds(List.of(id));
    assertThat(result).hasSize(1);
    assertThat(result.getFirst().id()).isEqualTo(id);
  }

  @Test
  @DisplayName("Should return empty list for empty IDs")
  void listViewsByIdsEmpty() {
    assertThat(service.listViewsByIds(List.of())).isEmpty();
  }

  @Test
  @DisplayName("Should delegate complex search")
  void search() {
    EntityComplexSearchView view =
        new EntityComplexSearchView(
            UuidCreator.getTimeOrderedEpoch(),
            TestBrazilianIdentifierGenerator.generateValidCnpj(),
            "WEG S.A.",
            "Addr",
            UuidCreator.getTimeOrderedEpoch(),
            "Blumenau",
            "1234567",
            OffsetDateTime.now(),
            OffsetDateTime.now());
    PageResult<EntityComplexSearchView> result = new PageResult<>(List.of(view), 0, 25, 1, 1);
    PageQuery pageQuery = new PageQuery(0, 25);
    EntityComplexSearchCriteria criteria =
        new EntityComplexSearchCriteria("weg", null, null, null, null, null);

    when(queries.search(pageQuery, criteria)).thenReturn(result);

    assertThat(service.search(pageQuery, criteria)).isEqualTo(result);
  }
}
