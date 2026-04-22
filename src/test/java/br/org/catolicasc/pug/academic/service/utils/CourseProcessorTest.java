package br.org.catolicasc.pug.academic.service.utils;

import static org.assertj.core.api.Assertions.assertThat;

import br.org.catolicasc.pug.academic.domain.Course;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("CourseProcessor Coverage")
class CourseProcessorTest {

  @Test
  @DisplayName("Should process create input successfully")
  void shouldProcessCreateInput() {
    UUID schoolId = UUID.randomUUID();
    Course course = CourseProcessor.processCreateInput("Computer Science", schoolId);

    assertThat(course.hasFieldErrors()).isFalse();
    assertThat(course.getName()).isEqualTo("Computer Science");
    assertThat(course.getSchoolId()).isEqualTo(schoolId);
  }

  @Test
  @DisplayName("Should collect errors for blank name and null schoolId")
  void shouldCollectErrors() {
    Course course = CourseProcessor.processCreateInput("", null);

    assertThat(course.hasFieldErrors()).isTrue();
  }

  @Test
  @DisplayName("Should update name via processUpdateInput")
  void shouldUpdateName() {
    Course existing = Course.factory("Old Name", UUID.randomUUID());
    Course updated = CourseProcessor.processUpdateInput(existing, "New Name", null);

    assertThat(updated.getName()).isEqualTo("New Name");
    assertThat(updated.getSchoolId()).isEqualTo(existing.getSchoolId());
  }

  @Test
  @DisplayName("Should update schoolId via processUpdateInput")
  void shouldUpdateSchoolId() {
    Course existing = Course.factory("Course", UUID.randomUUID());
    UUID newSchoolId = UUID.randomUUID();
    Course updated = CourseProcessor.processUpdateInput(existing, null, newSchoolId);

    assertThat(updated.getSchoolId()).isEqualTo(newSchoolId);
    assertThat(updated.getName()).isEqualTo("Course");
  }

  @Test
  @DisplayName("Should skip update if all inputs are null/empty")
  void shouldSkipNullUpdates() {
    Course existing = Course.factory("Course", UUID.randomUUID());
    Course updated = CourseProcessor.processUpdateInput(existing, null, null);

    assertThat(updated).isEqualTo(existing);
  }
}
