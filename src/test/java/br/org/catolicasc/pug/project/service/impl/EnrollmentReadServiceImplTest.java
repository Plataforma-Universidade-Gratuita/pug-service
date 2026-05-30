package br.org.catolicasc.pug.project.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import br.org.catolicasc.pug.project.domain.enums.EnrollmentStatus;
import br.org.catolicasc.pug.project.infra.read.EnrollmentsQueries;
import br.org.catolicasc.pug.project.infra.read.dtos.EnrollmentView;
import br.org.catolicasc.pug.project.service.dtos.enrollments.EnrollmentComplexSearchCriteria;
import br.org.catolicasc.pug.shared.domain.enums.Campi;
import br.org.catolicasc.pug.shared.exceptions.ResourceNotFoundException;
import br.org.catolicasc.pug.shared.service.dtos.PageQuery;
import br.org.catolicasc.pug.shared.service.dtos.PageResult;
import com.github.f4b6a3.uuid.UuidCreator;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@QuarkusTest
@DisplayName("EnrollmentsReadServiceImpl Coverage")
class EnrollmentReadServiceImplTest {

  @Inject EnrollmentsReadServiceImpl service;
  @InjectMock EnrollmentsQueries queries;

  private EnrollmentView sampleView() {
    return new EnrollmentView(
        UuidCreator.getTimeOrderedEpoch(),
        "Project Name",
        UuidCreator.getTimeOrderedEpoch(),
        "Student Name",
        "student@example.com",
        "20260001",
        Campi.JOINVILLE,
        LocalDate.now(),
        LocalDate.now().plusMonths(6),
        EnrollmentStatus.PENDING,
        OffsetDateTime.now(),
        OffsetDateTime.now(),
        null,
        null);
  }

  @Test
  @DisplayName("Should return enrollment view by IDs")
  void getViewByIdsSuccess() {
    UUID pid = UuidCreator.getTimeOrderedEpoch();
    UUID sid = UuidCreator.getTimeOrderedEpoch();
    EnrollmentView view = sampleView();
    when(queries.findOptionalByIds(pid, sid)).thenReturn(Optional.of(view));

    assertThat(service.getViewByIds(pid, sid)).isEqualTo(view);
  }

  @Test
  @DisplayName("Should throw ResourceNotFound when IDs not found")
  void getViewByIdsNotFound() {
    when(queries.findOptionalByIds(any(), any())).thenReturn(Optional.empty());
    assertThrows(
        ResourceNotFoundException.class,
        () ->
            service.getViewByIds(
                UuidCreator.getTimeOrderedEpoch(), UuidCreator.getTimeOrderedEpoch()));
  }

  @Test
  @DisplayName("Should list all enrollment views")
  void listViews() {
    when(queries.listAll()).thenReturn(List.of(sampleView()));
    assertThat(service.listViews()).hasSize(1);
  }

  @Test
  @DisplayName("Should list views by project ID")
  void listViewsByProjectId() {
    UUID pid = UuidCreator.getTimeOrderedEpoch();
    when(queries.listAllByProjectId(pid)).thenReturn(List.of(sampleView()));
    assertThat(service.listViewsByProjectId(pid)).hasSize(1);
  }

  @Test
  @DisplayName("Should return empty list for null project ID")
  void listViewsByProjectIdNull() {
    assertThat(service.listViewsByProjectId(null)).isEmpty();
  }

  @Test
  @DisplayName("Should list views by formerStudent ID")
  void listViewsByFormerStudentId() {
    UUID sid = UuidCreator.getTimeOrderedEpoch();
    when(queries.listAllByFormerStudentId(sid)).thenReturn(List.of(sampleView()));
    assertThat(service.listViewsByFormerStudentId(sid)).hasSize(1);
  }

  @Test
  @DisplayName("Should return empty list for null formerStudent ID")
  void listViewsByFormerStudentIdNull() {
    assertThat(service.listViewsByFormerStudentId(null)).isEmpty();
  }

  @Test
  @DisplayName("Should delegate paginated search")
  void search() {
    EnrollmentComplexSearchCriteria criteria =
        new EnrollmentComplexSearchCriteria(
            List.of(), List.of(), List.of(), null, null, null, null);
    PageQuery pageQuery = new PageQuery(0, 25);
    PageResult<EnrollmentView> result = new PageResult<>(List.of(sampleView()), 0, 25, 1, 1);

    when(queries.search(criteria, pageQuery)).thenReturn(result);

    assertThat(service.search(criteria, pageQuery)).isEqualTo(result);
  }
}
