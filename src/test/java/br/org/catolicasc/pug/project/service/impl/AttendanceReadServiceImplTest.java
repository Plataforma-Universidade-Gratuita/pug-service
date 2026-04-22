package br.org.catolicasc.pug.project.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import br.org.catolicasc.pug.project.domain.enums.AttendanceStatus;
import br.org.catolicasc.pug.project.infra.read.AttendanceQueries;
import br.org.catolicasc.pug.project.infra.read.dtos.AttendanceView;
import br.org.catolicasc.pug.shared.exceptions.ResourceNotFoundException;
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
@DisplayName("AttendanceReadServiceImpl Coverage")
class AttendanceReadServiceImplTest {

  @Inject AttendanceReadServiceImpl service;
  @InjectMock AttendanceQueries queries;

  private AttendanceView sampleView() {
    return new AttendanceView(
        UUID.randomUUID(),
        UUID.randomUUID(),
        UUID.randomUUID(),
        new BigDecimal("2.00"),
        "hash-123",
        AttendanceStatus.WAITING,
        null,
        null,
        OffsetDateTime.now(),
        OffsetDateTime.now());
  }

  @Test
  @DisplayName("Should return attendance view by ID")
  void getViewByIdSuccess() {
    UUID id = UUID.randomUUID();
    AttendanceView view = sampleView();
    when(queries.findOptionalById(id)).thenReturn(Optional.of(view));

    assertThat(service.getViewById(id)).isEqualTo(view);
  }

  @Test
  @DisplayName("Should throw ResourceNotFound when ID not found")
  void getViewByIdNotFound() {
    when(queries.findOptionalById(any())).thenReturn(Optional.empty());
    assertThrows(ResourceNotFoundException.class, () -> service.getViewById(UUID.randomUUID()));
  }

  @Test
  @DisplayName("Should list by enrollment ID")
  void listByEnrollmentId() {
    UUID pid = UUID.randomUUID();
    UUID sid = UUID.randomUUID();
    when(queries.listByEnrollmentId(pid, sid)).thenReturn(List.of(sampleView()));
    assertThat(service.listByEnrollmentId(pid, sid)).hasSize(1);
  }

  @Test
  @DisplayName("Should return empty list for null enrollment project ID")
  void listByEnrollmentIdNullProject() {
    assertThat(service.listByEnrollmentId(null, UUID.randomUUID())).isEmpty();
  }

  @Test
  @DisplayName("Should return empty list for null enrollment student ID")
  void listByEnrollmentIdNullStudent() {
    assertThat(service.listByEnrollmentId(UUID.randomUUID(), null)).isEmpty();
  }

  @Test
  @DisplayName("Should list by project ID")
  void listByProjectId() {
    UUID pid = UUID.randomUUID();
    when(queries.listByProjectId(pid)).thenReturn(List.of(sampleView()));
    assertThat(service.listByProjectId(pid)).hasSize(1);
  }

  @Test
  @DisplayName("Should return empty list for null project ID")
  void listByProjectIdNull() {
    assertThat(service.listByProjectId(null)).isEmpty();
  }

  @Test
  @DisplayName("Should list by student ID")
  void listByStudentId() {
    UUID sid = UUID.randomUUID();
    when(queries.listByStudentId(sid)).thenReturn(List.of(sampleView()));
    assertThat(service.listByStudentId(sid)).hasSize(1);
  }

  @Test
  @DisplayName("Should return empty list for null student ID")
  void listByStudentIdNull() {
    assertThat(service.listByStudentId(null)).isEmpty();
  }

  @Test
  @DisplayName("Should list all views")
  void listViews() {
    when(queries.listViews()).thenReturn(List.of(sampleView()));
    assertThat(service.listViews()).hasSize(1);
  }
}
