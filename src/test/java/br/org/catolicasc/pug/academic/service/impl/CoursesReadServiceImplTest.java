package br.org.catolicasc.pug.academic.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import br.org.catolicasc.pug.academic.infra.read.CoursesQueries;
import br.org.catolicasc.pug.academic.infra.read.dtos.CourseView;
import br.org.catolicasc.pug.academic.infra.read.dtos.SchoolView;
import br.org.catolicasc.pug.academic.service.dtos.courses.CourseComplexSearchCriteria;
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
@DisplayName("CoursesReadServiceImpl Coverage")
class CoursesReadServiceImplTest {

  @Inject CoursesReadServiceImpl service;
  @InjectMock CoursesQueries queries;

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
  @DisplayName("Should list courses by IDs")
  void listViewsByIds() {
    UUID id = UuidCreator.getTimeOrderedEpoch();
    when(queries.listAllByIds(List.of(id))).thenReturn(List.of(buildView(id)));

    assertThat(service.listViewsByIds(List.of(id))).hasSize(1);
  }

  @Test
  @DisplayName("Should delegate paginated search")
  void search() {
    PageQuery pageQuery = new PageQuery(0, 25);
    CourseComplexSearchCriteria criteria = new CourseComplexSearchCriteria("Comp", List.of());
    PageResult<CourseView> expected =
        new PageResult<>(List.of(buildView(UuidCreator.getTimeOrderedEpoch())), 0, 25, 1, 1);
    when(queries.search(pageQuery, criteria)).thenReturn(expected);

    assertThat(service.search(pageQuery, criteria)).isEqualTo(expected);
  }

  private CourseView buildView(UUID id) {
    OffsetDateTime now = OffsetDateTime.now();
    SchoolView areaOfExpertise =
        new SchoolView(UuidCreator.getTimeOrderedEpoch(), "Engineering", now, now);
    return new CourseView(id, "Computer Science", areaOfExpertise, now, now);
  }
}
