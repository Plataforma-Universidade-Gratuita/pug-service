package br.org.catolicasc.pug.academic.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import br.org.catolicasc.pug.academic.infra.read.SchoolQueries;
import br.org.catolicasc.pug.academic.infra.read.dtos.SchoolView;
import br.org.catolicasc.pug.shared.exceptions.ResourceNotFoundException;
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
@DisplayName("SchoolReadServiceImpl Coverage")
class SchoolReadServiceImplTest {

  @Inject SchoolReadServiceImpl service;
  @InjectMock SchoolQueries queries;

  @Test
  @DisplayName("Should return areaOfExpertise view by ID")
  void getViewByIdSuccess() {
    UUID id = UuidCreator.getTimeOrderedEpoch();
    OffsetDateTime now = OffsetDateTime.now();
    SchoolView view = new SchoolView(id, "Engineering", now, now);
    when(queries.findOptionalById(id)).thenReturn(Optional.of(view));

    assertThat(service.getViewById(id)).isEqualTo(view);
  }

  @Test
  @DisplayName("Should throw when areaOfExpertise not found")
  void getViewByIdNotFound() {
    when(queries.findOptionalById(any())).thenReturn(Optional.empty());
    assertThrows(
        ResourceNotFoundException.class,
        () -> service.getViewById(UuidCreator.getTimeOrderedEpoch()));
  }

  @Test
  @DisplayName("Should list all areaOfExpertises")
  void listAll() {
    OffsetDateTime now = OffsetDateTime.now();
    when(queries.listAllSchools())
        .thenReturn(List.of(new SchoolView(UuidCreator.getTimeOrderedEpoch(), "Eng", now, now)));

    assertThat(service.listAll()).hasSize(1);
  }

  @Test
  @DisplayName("Should fold input and delegate search")
  void searchByName() {
    when(queries.searchByName("engenharia")).thenReturn(List.of());
    assertThat(service.searchByName("  Engenharia  ")).isEmpty();
  }
}
