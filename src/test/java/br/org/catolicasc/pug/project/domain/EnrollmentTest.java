package br.org.catolicasc.pug.project.domain;

import static org.assertj.core.api.Assertions.assertThat;

import br.org.catolicasc.pug.academic.domain.Student;
import br.org.catolicasc.pug.helpers.builders.ProjectBuilder;
import br.org.catolicasc.pug.helpers.builders.StudentBuilder;
import br.org.catolicasc.pug.project.domain.enums.EnrollmentStatus;
import br.org.catolicasc.pug.project.domain.enums.ProjectsFieldErrorCodes;
import br.org.catolicasc.pug.shared.exceptions.BusinessRuleException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("Enrollment Aggregate Tests")
class EnrollmentTest {

  private final Student student = StudentBuilder.aStudent().build();
  private final Project project = ProjectBuilder.aProject().build();

  @Nested
  @DisplayName("Factory and Validation")
  class FactoryTests {

    @Test
    @DisplayName("Should create valid Enrollment in PENDING status")
    void shouldCreateValidEnrollment() {
      Enrollment enrollment = Enrollment.factory(student, project);

      assertThat(enrollment.hasFieldErrors()).isFalse();
      assertThat(enrollment.getStatus()).isEqualTo(EnrollmentStatus.PENDING);
      assertThat(enrollment.getIdentifier()).isNotNull();
    }

    @Test
    @DisplayName("Should collect errors when data is null")
    void shouldCollectValidationErrors() {
      Enrollment enrollment = Enrollment.factory(null, null);

      assertThat(enrollment.hasFieldErrors()).isTrue();
      assertThat(enrollment.getFieldErrors())
          .contains(
              ProjectsFieldErrorCodes.INVALID_ENROLLMENT_STUDENT_BLANK,
              ProjectsFieldErrorCodes.INVALID_ENROLLMENT_PROJECT_BLANK);
    }
  }

  @Nested
  @DisplayName("Behavior Methods")
  class BehaviorTests {

    @Test
    @DisplayName("Should transition from PENDING to APPROVED successfully")
    void shouldApproveEnrollment() {
      Enrollment enrollment = Enrollment.factory(student, project);

      Enrollment approved = enrollment.changeStatus(EnrollmentStatus.APPROVED);

      assertThat(approved.getStatus()).isEqualTo(EnrollmentStatus.APPROVED);
      assertThat(approved.getEnrollmentInfo().getAcceptedAt()).isNotNull();
    }

    @Test
    @DisplayName("Should throw BusinessRuleException when transitioning invalidly")
    void shouldRejectInvalidTransition() {
      Enrollment enrollment = Enrollment.factory(student, project);

      Assertions.assertThrows(
          BusinessRuleException.class, () -> enrollment.changeStatus(EnrollmentStatus.COMPLETED));
    }

    @Test
    @DisplayName("Should transition from APPROVED to COMPLETED (closing status)")
    void shouldCloseEnrollment() {
      Enrollment enrollment =
          Enrollment.factory(student, project).changeStatus(EnrollmentStatus.APPROVED);

      Enrollment completed = enrollment.changeStatus(EnrollmentStatus.COMPLETED);

      assertThat(completed.getStatus()).isEqualTo(EnrollmentStatus.COMPLETED);
      assertThat(completed.getEnrollmentInfo().getClosingStatusAt()).isNotNull();
    }
  }
}
