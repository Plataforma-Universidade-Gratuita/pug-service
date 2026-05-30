package br.org.catolicasc.pug.project.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import br.org.catolicasc.pug.project.domain.enums.AttendanceStatus;
import br.org.catolicasc.pug.project.infra.read.AttendancesQueries;
import br.org.catolicasc.pug.project.infra.read.dtos.AttendanceView;
import br.org.catolicasc.pug.project.service.dtos.attendance.AttendanceComplexSearchCriteria;
import br.org.catolicasc.pug.shared.domain.enums.Campi;
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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@QuarkusTest
@DisplayName("AttendancesReadServiceImpl Coverage")
class AttendanceReadServiceImplTest {

  @Inject AttendancesReadServiceImpl service;
  @InjectMock AttendancesQueries queries;

  @Test
  @DisplayName("Should return attendance view by ID")
  void getViewByIdSuccess() {
    UUID id = UuidCreator.getTimeOrderedEpoch();
    AttendanceView view = sampleView();
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
  @DisplayName("Should list all views")
  void listViews() {
    when(queries.listAll()).thenReturn(List.of(sampleView()));
    assertThat(service.listViews()).hasSize(1);
  }

  @Test
  @DisplayName("Should list views by IDs")
  void listViewsByIds() {
    UUID id = UuidCreator.getTimeOrderedEpoch();
    when(queries.listAllByIds(List.of(id))).thenReturn(List.of(sampleView()));
    assertThat(service.listViewsByIds(List.of(id))).hasSize(1);
  }

  @Test
  @DisplayName("Should delegate search")
  void search() {
    AttendanceComplexSearchCriteria criteria =
        new AttendanceComplexSearchCriteria(
            List.of(), List.of(), List.of(), List.of(), null, null, null, null);
    PageQuery pageQuery = new PageQuery(0, 25);
    PageResult<AttendanceView> result = new PageResult<>(List.of(sampleView()), 0, 25, 1, 1);

    when(queries.search(criteria, pageQuery)).thenReturn(result);

    assertThat(service.search(criteria, pageQuery)).isEqualTo(result);
  }

  private AttendanceView sampleView() {
    OffsetDateTime now = OffsetDateTime.now();
    return new AttendanceView(
        UuidCreator.getTimeOrderedEpoch(),
        UuidCreator.getTimeOrderedEpoch(),
        "Project Name",
        UuidCreator.getTimeOrderedEpoch(),
        "Student Name",
        "student@example.com",
        "20260001",
        Campi.JOINVILLE,
        new BigDecimal("2.00"),
        "hash-123",
        AttendanceStatus.WAITING,
        null,
        null,
        null,
        null,
        now,
        now);
  }
}
