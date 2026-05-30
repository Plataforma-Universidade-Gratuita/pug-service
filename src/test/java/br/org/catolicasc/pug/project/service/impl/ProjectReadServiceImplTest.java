package br.org.catolicasc.pug.project.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import br.org.catolicasc.pug.project.domain.enums.ProjectStatus;
import br.org.catolicasc.pug.project.infra.read.ProjectQueries;
import br.org.catolicasc.pug.project.infra.read.dtos.ProjectView;
import br.org.catolicasc.pug.project.service.dtos.ProjectComplexSearchCriteria;
import br.org.catolicasc.pug.shared.exceptions.ResourceNotFoundException;
import br.org.catolicasc.pug.shared.service.dtos.PageQuery;
import br.org.catolicasc.pug.shared.service.dtos.PageResult;
import com.github.f4b6a3.uuid.UuidCreator;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;

@QuarkusTest
class ProjectReadServiceImplTest {

  @Inject ProjectReadServiceImpl service;
  @InjectMock ProjectQueries queries;

  @Test
  void getViewByIdSuccess() {
    UUID id = UuidCreator.getTimeOrderedEpoch();
    when(queries.findOptionalById(id)).thenReturn(Optional.of(sampleView()));
    assertThat(service.getViewById(id)).isNotNull();
  }

  @Test
  void getViewByIdNotFound() {
    when(queries.findOptionalById(any())).thenReturn(Optional.empty());
    assertThrows(ResourceNotFoundException.class, () -> service.getViewById(UuidCreator.getTimeOrderedEpoch()));
  }

  @Test
  void listViewsByIdsFallsBackToAll() {
    when(queries.listAll()).thenReturn(List.of(sampleView()));
    assertThat(service.listViewsByIds(List.of())).hasSize(1);
  }

  @Test
  void searchDelegates() {
    when(queries.search(any(), any())).thenReturn(new PageResult<>(List.of(sampleView()), 0, 1, 1, 1));
    assertThat(service.search(new ProjectComplexSearchCriteria(null, List.of(), null, List.of(), null, null, List.of(), null, null), new PageQuery(0, 1)).content()).hasSize(1);
  }

  private ProjectView sampleView() {
    return new ProjectView(
        UuidCreator.getTimeOrderedEpoch(),
        "Test Project",
        UuidCreator.getTimeOrderedEpoch(),
        "Entity Name",
        "desc",
        UuidCreator.getTimeOrderedEpoch(),
        20,
        new BigDecimal("40.00"),
        BigDecimal.ZERO,
        ProjectStatus.PLANNED,
        null,
        OffsetDateTime.now(),
        OffsetDateTime.now());
  }
}
