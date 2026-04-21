package br.org.catolicasc.pug.academic.infra.read.impl;

import static org.assertj.core.api.Assertions.assertThat;

import br.org.catolicasc.pug.academic.domain.Course;
import br.org.catolicasc.pug.academic.domain.School;
import br.org.catolicasc.pug.helpers.TestDataFactory;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@QuarkusTest
class CourseQueriesImplTest {

  @Inject CourseQueriesImpl queries;
  @Inject TestDataFactory factory;

  private School school;

  @BeforeEach
  void setup() {
    school = factory.createSchool();
  }

  @Test
  @Transactional
  @DisplayName("Should find CourseView with School details")
  void shouldFindWithSchoolDetails() {
    Course course = factory.createCourse(school);

    var view = queries.findOptionalById(course.getId());

    assertThat(view).isPresent();
    assertThat(view.get().school().name()).isEqualTo(school.getName());
  }
}
