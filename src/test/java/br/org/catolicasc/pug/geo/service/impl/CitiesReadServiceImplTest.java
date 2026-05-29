package br.org.catolicasc.pug.geo.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import br.org.catolicasc.pug.geo.infra.read.CitiesQueries;
import br.org.catolicasc.pug.geo.infra.read.dtos.CityView;
import br.org.catolicasc.pug.geo.service.dtos.CityComplexSearchCriteria;
import br.org.catolicasc.pug.shared.exceptions.ResourceNotFoundException;
import br.org.catolicasc.pug.shared.service.dtos.PageQuery;
import br.org.catolicasc.pug.shared.service.dtos.PageResult;
import com.github.f4b6a3.uuid.UuidCreator;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@QuarkusTest
@DisplayName("CitiesReadServiceImpl Coverage")
class CitiesReadServiceImplTest {

  @Inject CitiesReadServiceImpl service;
  @InjectMock CitiesQueries queries;

  @Test
  @DisplayName("Should return city view by ID")
  void getByIdSuccess() {
    UUID id = UuidCreator.getTimeOrderedEpoch();
    CityView view = new CityView(id, "Joinville", "4209106");
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
  @DisplayName("Should list all city views")
  void listViews() {
    when(queries.listAllCities())
        .thenReturn(
            List.of(new CityView(UuidCreator.getTimeOrderedEpoch(), "Joinville", "4209106")));
    assertThat(service.listViews()).hasSize(1);
  }

  @Test
  @DisplayName("Should execute paginated city search")
  void search() {
    PageQuery pageQuery = new PageQuery(0, 10);
    PageResult<CityView> pageResult =
        new PageResult<>(
            List.of(new CityView(UuidCreator.getTimeOrderedEpoch(), "Joinville", "4209106")),
            0,
            10,
            1,
            1);
    when(queries.search(any(), any())).thenReturn(pageResult);

    assertThat(service.search(pageQuery, new CityComplexSearchCriteria("Joinville")))
        .isEqualTo(pageResult);
  }

  @Test
  @DisplayName("Should list city views by IDs successfully")
  void listViewsByIdsSuccess() {
    UUID id = UuidCreator.getTimeOrderedEpoch();
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
