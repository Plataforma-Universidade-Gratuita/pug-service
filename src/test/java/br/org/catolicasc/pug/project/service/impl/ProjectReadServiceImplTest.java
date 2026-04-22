package br.org.catolicasc.pug.project.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import br.org.catolicasc.pug.project.domain.enums.ProjectStatus;
import br.org.catolicasc.pug.project.infra.read.ProjectQueries;
import br.org.catolicasc.pug.project.infra.read.dtos.ProjectView;
import br.org.catolicasc.pug.shared.exceptions.ResourceNotFoundException;
import com.github.f4b6a3.uuid.UuidCreator;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@QuarkusTest
@DisplayName("ProjectReadServiceImpl Coverage")
class ProjectReadServiceImplTest {

  @Inject ProjectReadServiceImpl service;
  @InjectMock ProjectQueries queries;

  private ProjectView sampleView() {
    return new ProjectView(
        UuidCreator.getTimeOrderedEpoch(),
        "Test Project",
        UuidCreator.getTimeOrderedEpoch(),
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

  @Test
  @DisplayName("Should return project view by ID")
  void getViewByIdSuccess() {
    UUID id = UuidCreator.getTimeOrderedEpoch();
    ProjectView view = sampleView();
    when(queries.findOptionalById(id)).thenReturn(Optional.of(view));

    assertThat(service.getViewById(id)).isEqualTo(view);
  }

  @Test
  @DisplayName("Should throw ResourceNotFound when ID not found")
  void getViewByIdNotFound() {
    when(queries.findOptionalById(any())).thenReturn(Optional.empty());
    assertThrows(
        ResourceNotFoundException.class,
        () -> service.getViewById(UuidCreator.getTimeOrderedEpoch()));
  }

  @Test
  @DisplayName("Should list all project views")
  void listViews() {
    ProjectView view = sampleView();
    when(queries.listAllProjects()).thenReturn(List.of(view));

    List<ProjectView> result = service.listViews();
    assertThat(result).hasSize(1);
  }

  @Test
  @DisplayName("Should list views by creator ID")
  void listViewsByCreatedBy() {
    UUID accountId = UuidCreator.getTimeOrderedEpoch();
    ProjectView view = sampleView();
    when(queries.listByCreatedBy(accountId)).thenReturn(List.of(view));

    assertThat(service.listViewsByCreatedBy(accountId)).hasSize(1);
  }

  @Test
  @DisplayName("Should return empty list for null creator ID")
  void listViewsByCreatedByNull() {
    assertThat(service.listViewsByCreatedBy(null)).isEmpty();
  }

  @Test
  @DisplayName("Should list views by entity ID")
  void listViewsByEntityId() {
    UUID entityId = UuidCreator.getTimeOrderedEpoch();
    ProjectView view = sampleView();
    when(queries.listByEntityId(entityId)).thenReturn(List.of(view));

    assertThat(service.listViewsByEntityId(entityId)).hasSize(1);
  }

  @Test
  @DisplayName("Should return empty list for null entity ID")
  void listViewsByEntityIdNull() {
    assertThat(service.listViewsByEntityId(null)).isEmpty();
  }

  @Test
  @DisplayName("Should search by name and delegate folded query")
  void searchByName() {
    when(queries.searchByName("test")).thenReturn(List.of(sampleView()));
    assertThat(service.searchByName("  TEST  ")).hasSize(1);
  }

  @Test
  @DisplayName("Should return empty list for null search query")
  void searchByNameNull() {
    assertThat(service.searchByName(null)).isEmpty();
  }

  @Test
  @DisplayName("Should return empty list for empty search query")
  void searchByNameEmpty() {
    assertThat(service.searchByName("")).isEmpty();
  }
}
