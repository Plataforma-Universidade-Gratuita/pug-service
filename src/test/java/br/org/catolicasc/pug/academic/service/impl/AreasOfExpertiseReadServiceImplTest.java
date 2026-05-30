package br.org.catolicasc.pug.academic.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import br.org.catolicasc.pug.academic.infra.read.AreasOfExpertiseQueries;
import br.org.catolicasc.pug.academic.infra.read.dtos.SchoolView;
import br.org.catolicasc.pug.academic.service.dtos.areasofexpertise.AreaOfExpertiseComplexSearchCriteria;
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
@DisplayName("AreasOfExpertiseReadServiceImpl Coverage")
class AreasOfExpertiseReadServiceImplTest {

  @Inject AreasOfExpertiseReadServiceImpl service;
  @InjectMock AreasOfExpertiseQueries queries;

  @Test
  @DisplayName("Should return area-of-expertise view by ID")
  void getViewByIdSuccess() {
    UUID id = UuidCreator.getTimeOrderedEpoch();
    SchoolView view = new SchoolView(id, "Engineering", OffsetDateTime.now(), OffsetDateTime.now());
    when(queries.findOptionalById(id)).thenReturn(Optional.of(view));
    assertThat(service.getViewById(id)).isEqualTo(view);
  }

  @Test
  @DisplayName("Should throw when area of expertise is not found")
  void getViewByIdNotFound() {
    when(queries.findOptionalById(any())).thenReturn(Optional.empty());
    assertThrows(ResourceNotFoundException.class, () -> service.getViewById(UuidCreator.getTimeOrderedEpoch()));
  }

  @Test
  @DisplayName("Should list all areas of expertise")
  void listViews() {
    when(queries.listAllViews())
        .thenReturn(List.of(new SchoolView(UuidCreator.getTimeOrderedEpoch(), "Eng", OffsetDateTime.now(), OffsetDateTime.now())));
    assertThat(service.listViews()).hasSize(1);
  }

  @Test
  @DisplayName("Should list areas of expertise by IDs")
  void listViewsByIds() {
    UUID id = UuidCreator.getTimeOrderedEpoch();
    when(queries.listAllByIds(List.of(id)))
        .thenReturn(List.of(new SchoolView(id, "Eng", OffsetDateTime.now(), OffsetDateTime.now())));
    assertThat(service.listViewsByIds(List.of(id))).hasSize(1);
  }

  @Test
  @DisplayName("Should delegate paginated search")
  void search() {
    PageQuery pageQuery = new PageQuery(0, 25);
    AreaOfExpertiseComplexSearchCriteria criteria = new AreaOfExpertiseComplexSearchCriteria("Eng");
    PageResult<SchoolView> expected =
        new PageResult<>(
            List.of(new SchoolView(UuidCreator.getTimeOrderedEpoch(), "Eng", OffsetDateTime.now(), OffsetDateTime.now())),
            0,
            25,
            1,
            1);
    when(queries.search(pageQuery, criteria)).thenReturn(expected);
    assertThat(service.search(pageQuery, criteria)).isEqualTo(expected);
  }
}
