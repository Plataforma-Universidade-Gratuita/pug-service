package br.org.catolicasc.pug.academic.service.utils;

import static org.assertj.core.api.Assertions.assertThat;

import br.org.catolicasc.pug.academic.domain.Course;
import com.github.f4b6a3.uuid.UuidCreator;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("CourseProcessor Coverage")
class CourseProcessorTest {

  @Test
  @DisplayName("Should process create input successfully")
  void shouldProcessCreateInput() {
    UUID areaOfExpertiseId = UuidCreator.getTimeOrderedEpoch();
    Course course = CourseProcessor.processCreateInput("Computer Science", areaOfExpertiseId);

    assertThat(course.hasFieldErrors()).isFalse();
    assertThat(course.getName()).isEqualTo("Computer Science");
    assertThat(course.getAreaOfExpertiseId()).isEqualTo(areaOfExpertiseId);
  }

  @Test
  @DisplayName("Should collect errors for blank name and null areaOfExpertiseId")
  void shouldCollectErrors() {
    Course course = CourseProcessor.processCreateInput("", null);

    assertThat(course.hasFieldErrors()).isTrue();
  }

  @Test
  @DisplayName("Should update name via processUpdateInput")
  void shouldUpdateName() {
    Course existing = Course.factory("Old Name", UuidCreator.getTimeOrderedEpoch());
    Course updated = CourseProcessor.processUpdateInput(existing, "New Name", null);

    assertThat(updated.getName()).isEqualTo("New Name");
    assertThat(updated.getAreaOfExpertiseId()).isEqualTo(existing.getAreaOfExpertiseId());
  }

  @Test
  @DisplayName("Should update areaOfExpertiseId via processUpdateInput")
  void shouldUpdateAreaOfExpertiseId() {
    Course existing = Course.factory("Course", UuidCreator.getTimeOrderedEpoch());
    UUID newAreaOfExpertiseId = UuidCreator.getTimeOrderedEpoch();
    Course updated = CourseProcessor.processUpdateInput(existing, null, newAreaOfExpertiseId);

    assertThat(updated.getAreaOfExpertiseId()).isEqualTo(newAreaOfExpertiseId);
    assertThat(updated.getName()).isEqualTo("Course");
  }

  @Test
  @DisplayName("Should skip update if all inputs are null/empty")
  void shouldSkipNullUpdates() {
    Course existing = Course.factory("Course", UuidCreator.getTimeOrderedEpoch());
    Course updated = CourseProcessor.processUpdateInput(existing, null, null);

    assertThat(updated).isEqualTo(existing);
  }
}
