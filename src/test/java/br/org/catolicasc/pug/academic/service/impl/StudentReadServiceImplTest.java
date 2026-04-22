package br.org.catolicasc.pug.academic.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import br.org.catolicasc.pug.academic.infra.read.StudentQueries;
import br.org.catolicasc.pug.academic.infra.read.dtos.StudentView;
import br.org.catolicasc.pug.shared.domain.enums.Campi;
import br.org.catolicasc.pug.shared.exceptions.ResourceNotFoundException;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@QuarkusTest
@DisplayName("StudentReadServiceImpl Coverage")
class StudentReadServiceImplTest {

  @Inject StudentReadServiceImpl service;
  @InjectMock StudentQueries queries;

  @Test
  @DisplayName("Should return student view by account ID")
  void getViewByAccountIdSuccess() {
    UUID id = UUID.randomUUID();
    StudentView view = buildView(id);
    when(queries.findOptionalById(id)).thenReturn(Optional.of(view));

    assertThat(service.getViewByAccountId(id)).isEqualTo(view);
  }

  @Test
  @DisplayName("Should throw when account ID not found")
  void getViewByAccountIdNotFound() {
    when(queries.findOptionalById(any())).thenReturn(Optional.empty());
    assertThrows(
        ResourceNotFoundException.class, () -> service.getViewByAccountId(UUID.randomUUID()));
  }

  @Test
  @DisplayName("Should return student view by registration")
  void getViewByAcademicRegistrationSuccess() {
    StudentView view = buildView(UUID.randomUUID());
    when(queries.findOptionalByAcademicRegistration("REG123")).thenReturn(Optional.of(view));

    assertThat(service.getViewByAcademicRegistration("REG123")).isEqualTo(view);
  }

  @Test
  @DisplayName("Should throw when registration not found")
  void getViewByAcademicRegistrationNotFound() {
    when(queries.findOptionalByAcademicRegistration(any())).thenReturn(Optional.empty());
    assertThrows(
        ResourceNotFoundException.class, () -> service.getViewByAcademicRegistration("NOTFOUND"));
  }

  @Test
  @DisplayName("Should return student view by CPF")
  void getViewByCpfSuccess() {
    StudentView view = buildView(UUID.randomUUID());
    when(queries.findOptionalByCpf("12345678901")).thenReturn(Optional.of(view));

    assertThat(service.getViewByCpf("12345678901")).isEqualTo(view);
  }

  @Test
  @DisplayName("Should throw when CPF not found")
  void getViewByCpfNotFound() {
    when(queries.findOptionalByCpf(any())).thenReturn(Optional.empty());
    assertThrows(ResourceNotFoundException.class, () -> service.getViewByCpf("00000000000"));
  }

  @Test
  @DisplayName("Should return student view by email")
  void getViewByEmailSuccess() {
    StudentView view = buildView(UUID.randomUUID());
    when(queries.findOptionalByEmail("john@test.com")).thenReturn(Optional.of(view));

    assertThat(service.getViewByEmail("john@test.com")).isEqualTo(view);
  }

  @Test
  @DisplayName("Should throw when email not found")
  void getViewByEmailNotFound() {
    when(queries.findOptionalByEmail(any())).thenReturn(Optional.empty());
    assertThrows(ResourceNotFoundException.class, () -> service.getViewByEmail("missing@test.com"));
  }

  @Test
  @DisplayName("Should list all students")
  void listViews() {
    when(queries.listAllStudents()).thenReturn(List.of(buildView(UUID.randomUUID())));
    assertThat(service.listViews()).hasSize(1);
  }

  @Test
  @DisplayName("Should list students by account IDs")
  void listViewsByAccountIds() {
    UUID id = UUID.randomUUID();
    when(queries.listViewsByAccountIds(List.of(id))).thenReturn(List.of(buildView(id)));
    assertThat(service.listViewsByAccountIds(List.of(id))).hasSize(1);
  }

  @Test
  @DisplayName("Should return empty for null account IDs")
  void listViewsByAccountIdsNull() {
    assertThat(service.listViewsByAccountIds(null)).isEmpty();
  }

  @Test
  @DisplayName("Should return empty for empty account IDs")
  void listViewsByAccountIdsEmpty() {
    assertThat(service.listViewsByAccountIds(List.of())).isEmpty();
  }

  @Test
  @DisplayName("Should list students by course ID")
  void listViewsByCourseId() {
    UUID courseId = UUID.randomUUID();
    when(queries.listAllByCourseId(courseId)).thenReturn(List.of(buildView(UUID.randomUUID())));
    assertThat(service.listViewsByCourseId(courseId)).hasSize(1);
  }

  @Test
  @DisplayName("Should return empty for null course ID")
  void listViewsByCourseIdNull() {
    assertThat(service.listViewsByCourseId(null)).isEmpty();
  }

  @Test
  @DisplayName("Should fold input and delegate search")
  void searchByName() {
    when(queries.searchByName("john")).thenReturn(List.of());
    assertThat(service.searchByName("  John  ")).isEmpty();
  }

  @Test
  @DisplayName("Should return empty for empty search query")
  void searchByNameEmpty() {
    assertThat(service.searchByName("")).isEmpty();
  }

  @Test
  @DisplayName("Should return empty for null search query")
  void searchByNameNull() {
    assertThat(service.searchByName(null)).isEmpty();
  }

  private StudentView buildView(UUID accountId) {
    OffsetDateTime now = OffsetDateTime.now();
    return new StudentView(
        accountId,
        "REG123",
        Campi.JOINVILLE,
        UUID.randomUUID(),
        new BigDecimal("100"),
        BigDecimal.ZERO,
        false,
        LocalDate.now(),
        LocalDate.now().plusMonths(6),
        now,
        now);
  }
}
