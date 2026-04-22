package br.org.catolicasc.pug.academic.infra.read.impl;

import static org.assertj.core.api.Assertions.assertThat;

import br.org.catolicasc.pug.academic.domain.Course;
import br.org.catolicasc.pug.academic.domain.School;
import br.org.catolicasc.pug.academic.infra.persistence.CourseEntity;
import br.org.catolicasc.pug.helpers.BaseSearchTest;
import br.org.catolicasc.pug.helpers.TestDataFactory;
import com.github.f4b6a3.uuid.UuidCreator;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@QuarkusTest
@DisplayName("CourseQueriesImpl Coverage")
class CourseQueriesImplTest extends BaseSearchTest {

  @Inject CourseQueriesImpl queries;
  @Inject TestDataFactory factory;

  private School school;
  private Course course;

  @BeforeEach
  void setup() {
    school = factory.createSchool();
    course = factory.createCourse(school);
  }

  @Test
  @Transactional
  @DisplayName("Should find CourseView with School details")
  void shouldFindWithSchoolDetails() {
    var view = queries.findOptionalById(course.getId());

    assertThat(view).isPresent();
    assertThat(view.get().school().name()).isEqualTo(school.getName());
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
  @DisplayName("Should list all courses by school ID")
  void shouldListAllBySchoolId() {
    var list = queries.listAllBySchoolId(school.getId());
    assertThat(list).isNotEmpty();
    assertThat(list).allSatisfy(v -> assertThat(v.school().name()).isEqualTo(school.getName()));
  }

  @Test
  @Transactional
  @DisplayName("Should return empty list for null school ID")
  void shouldReturnEmptyForNullSchoolId() {
    assertThat(queries.listAllBySchoolId(null)).isEmpty();
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
  void shouldSearchByNameSuccess() throws Exception {
    syncIndex(CourseEntity.class);

    String searchKey = course.getName().substring(0, 3);
    var results = queries.searchByName(searchKey);

    assertThat(results).anyMatch(v -> v.id().equals(course.getId()));
  }

  @Test
  @DisplayName("Should handle invalid search inputs gracefully")
  void shouldHandleInvalidSearchInputs() {
    assertSearchHandlesInvalidInput(queries::searchByName);
  }
}
