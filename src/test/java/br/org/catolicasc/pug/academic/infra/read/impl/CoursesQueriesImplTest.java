package br.org.catolicasc.pug.academic.infra.read.impl;

import static org.assertj.core.api.Assertions.assertThat;

import br.org.catolicasc.pug.academic.domain.AreaOfExpertise;
import br.org.catolicasc.pug.academic.domain.Course;
import br.org.catolicasc.pug.academic.service.dtos.courses.CourseComplexSearchCriteria;
import br.org.catolicasc.pug.helpers.BaseSearchTest;
import br.org.catolicasc.pug.helpers.TestDataFactory;
import br.org.catolicasc.pug.shared.service.dtos.PageQuery;
import com.github.f4b6a3.uuid.UuidCreator;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@QuarkusTest
@DisplayName("CoursesQueriesImpl Coverage")
class CoursesQueriesImplTest extends BaseSearchTest {

  @Inject CoursesQueriesImpl queries;
  @Inject TestDataFactory factory;

  private AreaOfExpertise areaOfExpertise;
  private Course course;

  @BeforeEach
  void setup() {
    areaOfExpertise = factory.createAreaOfExpertise();
    course = factory.createCourse(areaOfExpertise);
  }

  @Test
  @Transactional
  @DisplayName("Should find CourseView with AreaOfExpertise details")
  void shouldFindWithAreaOfExpertiseDetails() {
    var view = queries.findOptionalById(course.getId());

    assertThat(view).isPresent();
    assertThat(view.get().areaOfExpertise().name()).isEqualTo(areaOfExpertise.getName());
  }

  @Test
  @Transactional
  @DisplayName("Should return empty when ID is null")
  void shouldReturnEmptyForNullId() {
    assertThat(queries.findOptionalById(null)).isEmpty();
  }

  @Test
  @Transactional
  @DisplayName("Should return empty for non-existent ID")
  void shouldReturnEmptyForNonExistentId() {
    assertThat(queries.findOptionalById(UuidCreator.getTimeOrderedEpoch())).isEmpty();
  }

  @Test
  @Transactional
  @DisplayName("Should list courses by IDs")
  void shouldListAllByIds() {
    var list = queries.listAllByIds(List.of(course.getId()));
    assertThat(list).hasSize(1);
    assertThat(list.get(0).id()).isEqualTo(course.getId());
  }

  @Test
  @Transactional
  @DisplayName("Should return empty list for null IDs")
  void shouldReturnEmptyForNullIds() {
    assertThat(queries.listAllByIds(null)).isEmpty();
  }

  @Test
  @Transactional
  @DisplayName("Should list all courses")
  void shouldListAllCourses() {
    var list = queries.listAllCourses();
    assertThat(list).isNotEmpty();
  }

  @Test
  @Transactional
  @DisplayName("Should find course view with all fields populated")
  void shouldHaveAllFieldsPopulated() {
    var view = queries.findOptionalById(course.getId());

    assertThat(view).isPresent();
    var cv = view.get();
    assertThat(cv.id()).isEqualTo(course.getId());
    assertThat(cv.name()).isEqualTo(course.getName());
    assertThat(cv.createdAt()).isNotNull();
    assertThat(cv.updatedAt()).isNotNull();
  }

  @Test
  @DisplayName("Should search courses by name successfully")
  void shouldSearchByNameSuccess() {
    String searchKey = course.getName().substring(0, 3);
    var result =
        queries.search(new PageQuery(0, 10), new CourseComplexSearchCriteria(searchKey, null));

    assertThat(result.content()).anyMatch(v -> v.id().equals(course.getId()));
    assertThat(result.page()).isZero();
    assertThat(result.size()).isEqualTo(10);
  }

  @Test
  @DisplayName("Should search courses by areaOfExpertise successfully")
  void shouldSearchByAreaOfExpertiseSuccess() {
    var result =
        queries.search(
            new PageQuery(0, 10),
            new CourseComplexSearchCriteria(null, List.of(areaOfExpertise.getId())));

    assertThat(result.content()).anyMatch(v -> v.id().equals(course.getId()));
  }

  @Test
  @DisplayName("Should return paginated course list when search criteria is null")
  void shouldHandleNullSearchCriteria() {
    var result = queries.search(new PageQuery(0, 10), null);
    assertThat(result.content()).hasSizeLessThanOrEqualTo(10);
  }

  @Test
  @DisplayName("Should return full result set when page size is the fetch-all sentinel")
  void shouldFetchAllWhenPageSizeIsOne() {
    String searchKey = course.getName().substring(0, 3);
    var result =
        queries.search(new PageQuery(4, 1), new CourseComplexSearchCriteria(searchKey, null));

    assertThat(result.page()).isZero();
    assertThat(result.totalPages()).isLessThanOrEqualTo(1);
    assertThat(result.content().size()).isEqualTo(result.totalElements());
    assertThat(result.size()).isEqualTo(Math.max((int) result.totalElements(), 1));
  }
}
