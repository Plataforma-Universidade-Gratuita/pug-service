package br.org.catolicasc.pug.project.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import br.org.catolicasc.pug.project.domain.enums.EnrollmentStatus;
import br.org.catolicasc.pug.project.infra.read.EnrollmentQueries;
import br.org.catolicasc.pug.project.infra.read.dtos.EnrollmentView;
import br.org.catolicasc.pug.shared.exceptions.ResourceNotFoundException;
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
@DisplayName("EnrollmentReadServiceImpl Coverage")
class EnrollmentReadServiceImplTest {

  @Inject EnrollmentReadServiceImpl service;
  @InjectMock EnrollmentQueries queries;

  private EnrollmentView sampleView() {
    return new EnrollmentView(
        UUID.randomUUID(),
        UUID.randomUUID(),
        EnrollmentStatus.PENDING,
        OffsetDateTime.now(),
        OffsetDateTime.now(),
        null,
        null);
  }

  @Test
  @DisplayName("Should return enrollment view by IDs")
  void getViewByIdsSuccess() {
    UUID pid = UUID.randomUUID();
    UUID sid = UUID.randomUUID();
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
        () -> service.getViewByIds(UUID.randomUUID(), UUID.randomUUID()));
  }

  @Test
  @DisplayName("Should list all enrollment views")
  void listViews() {
    when(queries.listAllEnrollments()).thenReturn(List.of(sampleView()));
    assertThat(service.listViews()).hasSize(1);
  }

  @Test
  @DisplayName("Should list views by project ID")
  void listViewsByProjectId() {
    UUID pid = UUID.randomUUID();
    when(queries.listByProjectId(pid)).thenReturn(List.of(sampleView()));
    assertThat(service.listViewsByProjectId(pid)).hasSize(1);
  }

  @Test
  @DisplayName("Should return empty list for null project ID")
  void listViewsByProjectIdNull() {
    assertThat(service.listViewsByProjectId(null)).isEmpty();
  }

  @Test
  @DisplayName("Should list views by student ID")
  void listViewsByStudentId() {
    UUID sid = UUID.randomUUID();
    when(queries.listByStudentId(sid)).thenReturn(List.of(sampleView()));
    assertThat(service.listViewsByStudentId(sid)).hasSize(1);
  }

  @Test
  @DisplayName("Should return empty list for null student ID")
  void listViewsByStudentIdNull() {
    assertThat(service.listViewsByStudentId(null)).isEmpty();
  }
}
