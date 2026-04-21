package br.org.catolicasc.pug.academic.domain;

import static org.assertj.core.api.Assertions.assertThat;

import br.org.catolicasc.pug.academic.domain.enums.AcademicFieldErrorCodes;
import br.org.catolicasc.pug.shared.domain.enums.SharedFieldErrorCodes;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("Course Aggregate Tests")
class CourseTest {

  @Test
  @DisplayName("Should create valid Course")
  void shouldCreateCourse() {
    UUID schoolId = UUID.randomUUID();
    Course course = Course.factory("Software Engineering", schoolId);

    assertThat(course.hasFieldErrors()).isFalse();
    assertThat(course.getName()).isEqualTo("Software Engineering");
    assertThat(course.getSchoolId()).isEqualTo(schoolId);
  }

  @Test
  @DisplayName("Should collect errors when data is invalid")
  void shouldCollectValidationErrors() {
    Course course = Course.factory("   ", null);

    assertThat(course.hasFieldErrors()).isTrue();
    assertThat(course.getFieldErrors())
        .contains(
            SharedFieldErrorCodes.INVALID_NAME_BLANK, AcademicFieldErrorCodes.INVALID_SCHOOL_BLANK);
  }

  @Nested
  @DisplayName("Behavior Methods")
  class BehaviorTests {

    @Test
    @DisplayName("Should rename course successfully")
    void shouldRename() {
      Course course = Course.factory("Original Name", UUID.randomUUID());
      Course renamed = course.rename("New Name");

      assertThat(renamed.getName()).isEqualTo("New Name");
      assertThat(renamed.getAuditInfo().getUpdatedAt())
          .isAfterOrEqualTo(course.getAuditInfo().getCreatedAt());
    }

    @Test
    @DisplayName("Should move course to another school successfully")
    void shouldMoveToSchool() {
      UUID originalSchool = UUID.randomUUID();
      UUID newSchool = UUID.randomUUID();
      Course course = Course.factory("Course Name", originalSchool);

      Course moved = course.moveToSchool(newSchool);

      assertThat(moved.getSchoolId()).isEqualTo(newSchool);
      assertThat(moved.getAuditInfo().getUpdatedAt())
          .isAfterOrEqualTo(course.getAuditInfo().getCreatedAt());
    }
  }
}
