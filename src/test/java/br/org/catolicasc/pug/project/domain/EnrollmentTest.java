package br.org.catolicasc.pug.project.domain;

import static org.assertj.core.api.Assertions.assertThat;

import br.org.catolicasc.pug.academic.domain.FormerStudent;
import br.org.catolicasc.pug.helpers.builders.domain.ProjectBuilder;
import br.org.catolicasc.pug.helpers.builders.domain.FormerStudentBuilder;
import br.org.catolicasc.pug.project.domain.enums.EnrollmentStatus;
import br.org.catolicasc.pug.project.domain.enums.ProjectsFieldErrorCodes;
import br.org.catolicasc.pug.shared.exceptions.BusinessRuleException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("Enrollment Aggregate Tests")
class EnrollmentTest {

  private final FormerStudent formerStudent = FormerStudentBuilder.aStudent().build();
  private final Project project = ProjectBuilder.aProject().build();

  @Nested
  @DisplayName("Factory and Validation")
  class FactoryTests {

    @Test
    @DisplayName("Should create valid Enrollment in PENDING status")
    void shouldCreateValidEnrollment() {
      Enrollment enrollment = Enrollment.factory(formerStudent, project);

      assertThat(enrollment.hasFieldErrors()).isFalse();
      assertThat(enrollment.getStatus()).isEqualTo(EnrollmentStatus.PENDING);
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
    @DisplayName("Should be idempotent when status is the same")
    void shouldBeIdempotent() {
      Enrollment enrollment = Enrollment.factory(formerStudent, project);
      assertThat(enrollment.changeStatus(EnrollmentStatus.PENDING)).isEqualTo(enrollment);
    }

    @Test
    @DisplayName("Should transition from PENDING to APPROVED successfully")
    void shouldApproveEnrollment() {
      Enrollment enrollment = Enrollment.factory(formerStudent, project);
      Enrollment approved = enrollment.changeStatus(EnrollmentStatus.APPROVED);

      assertThat(approved.getStatus()).isEqualTo(EnrollmentStatus.APPROVED);
      assertThat(approved.getEnrollmentInfo().getAcceptedAt()).isNotNull();
    }

    @Test
    @DisplayName("Should transition from APPROVED to various closing statuses")
    void shouldCloseEnrollment() {
      Enrollment base =
          Enrollment.factory(formerStudent, project).changeStatus(EnrollmentStatus.APPROVED);

      for (EnrollmentStatus status :
          new EnrollmentStatus[] {
            EnrollmentStatus.COMPLETED,
            EnrollmentStatus.CANCELED,
            EnrollmentStatus.EXITED,
            EnrollmentStatus.REJECTED,
            EnrollmentStatus.REMOVED
          }) {
        Enrollment closed = base.changeStatus(status);
        assertThat(closed.getStatus()).isEqualTo(status);
        assertThat(closed.getEnrollmentInfo().getClosingStatusAt()).isNotNull();
      }
    }

    @Test
    @DisplayName("Should transition from APPROVED to ON_HOLD and then back to APPROVED")
    void shouldPauseAndResumeEnrollment() {
      Enrollment approved =
          Enrollment.factory(formerStudent, project).changeStatus(EnrollmentStatus.APPROVED);

      Enrollment onHold = approved.changeStatus(EnrollmentStatus.ON_HOLD);
      Enrollment resumed = onHold.changeStatus(EnrollmentStatus.APPROVED);

      assertThat(onHold.getStatus()).isEqualTo(EnrollmentStatus.ON_HOLD);
      assertThat(onHold.getEnrollmentInfo().getAcceptedAt())
          .isEqualTo(approved.getEnrollmentInfo().getAcceptedAt());
      assertThat(resumed.getStatus()).isEqualTo(EnrollmentStatus.APPROVED);
      assertThat(resumed.getEnrollmentInfo().getAcceptedAt())
          .isEqualTo(approved.getEnrollmentInfo().getAcceptedAt());
    }

    @Test
    @DisplayName("Should fail when transitioning from closed state")
    void shouldFailFromClosed() {
      Enrollment completed =
          Enrollment.factory(formerStudent, project)
              .changeStatus(EnrollmentStatus.APPROVED)
              .changeStatus(EnrollmentStatus.COMPLETED);

      Assertions.assertThrows(
          BusinessRuleException.class, () -> completed.changeStatus(EnrollmentStatus.APPROVED));
    }

    @Test
    @DisplayName("Should fail for invalid status transitions")
    void shouldFailInvalidTransitions() {
      Enrollment pending = Enrollment.factory(formerStudent, project);

      Assertions.assertThrows(
          BusinessRuleException.class, () -> pending.changeStatus(EnrollmentStatus.COMPLETED));

      Enrollment approved = pending.changeStatus(EnrollmentStatus.APPROVED);

      Assertions.assertThrows(
          BusinessRuleException.class, () -> approved.changeStatus(EnrollmentStatus.PENDING));

      Assertions.assertThrows(NullPointerException.class, () -> approved.changeStatus(null));
    }
  }
}

