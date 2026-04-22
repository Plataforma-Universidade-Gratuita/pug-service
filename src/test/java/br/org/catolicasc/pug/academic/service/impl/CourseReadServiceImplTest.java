package br.org.catolicasc.pug.academic.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import br.org.catolicasc.pug.academic.infra.read.CourseQueries;
import br.org.catolicasc.pug.academic.infra.read.dtos.CourseView;
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
@DisplayName("CourseReadServiceImpl Coverage")
class CourseReadServiceImplTest {

  @Inject CourseReadServiceImpl service;
  @InjectMock CourseQueries queries;

  @Test
  @DisplayName("Should return course view by ID")
  void getViewByIdSuccess() {
    UUID id = UuidCreator.getTimeOrderedEpoch();
    CourseView view = buildView(id);
    when(queries.findOptionalById(id)).thenReturn(Optional.of(view));

    assertThat(service.getViewById(id)).isEqualTo(view);
  }

  @Test
  @DisplayName("Should throw when course not found")
  void getViewByIdNotFound() {
    when(queries.findOptionalById(any())).thenReturn(Optional.empty());
    assertThrows(
        ResourceNotFoundException.class,
        () -> service.getViewById(UuidCreator.getTimeOrderedEpoch()));
  }

  @Test
  @DisplayName("Should list all courses")
  void listViews() {
    when(queries.listAllCourses())
        .thenReturn(List.of(buildView(UuidCreator.getTimeOrderedEpoch())));
    assertThat(service.listViews()).hasSize(1);
  }

  @Test
  @DisplayName("Should list courses by school ID")
  void listViewsBySchoolId() {
    UUID schoolId = UuidCreator.getTimeOrderedEpoch();
    when(queries.listAllBySchoolId(schoolId))
        .thenReturn(List.of(buildView(UuidCreator.getTimeOrderedEpoch())));

    assertThat(service.listViewsBySchoolId(schoolId)).hasSize(1);
  }

  @Test
  @DisplayName("Should return empty list for null school ID")
  void listViewsBySchoolIdNull() {
    assertThat(service.listViewsBySchoolId(null)).isEmpty();
  }

  @Test
  @DisplayName("Should fold input and delegate search")
  void searchByName() {
    when(queries.searchByName("computer")).thenReturn(List.of());
    assertThat(service.searchByName("  Computer  ")).isEmpty();
  }

  private CourseView buildView(UUID id) {
    OffsetDateTime now = OffsetDateTime.now();
    SchoolView school = new SchoolView(UuidCreator.getTimeOrderedEpoch(), "Eng", now, now);
    return new CourseView(id, "CS", school, now, now);
  }
}
