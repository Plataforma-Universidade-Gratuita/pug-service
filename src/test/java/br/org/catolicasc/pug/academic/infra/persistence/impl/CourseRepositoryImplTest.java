package br.org.catolicasc.pug.academic.infra.persistence.impl;

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
class CourseRepositoryImplTest {

  @Inject CourseRepositoryImpl repository;
  @Inject TestDataFactory factory;
  private School school;

  @BeforeEach
  void setup() {
    school = factory.createSchool();
  }

  @Test
  @Transactional
  @DisplayName("Should persist and find Course linked to School")
  void shouldPersistAndFind() {
    Course course = factory.createCourse(school);

    var found = repository.findOptionalById(course.getId());
    assertThat(found).isPresent();
    assertThat(found.get().getSchoolId()).isEqualTo(school.getId());
  }
}
