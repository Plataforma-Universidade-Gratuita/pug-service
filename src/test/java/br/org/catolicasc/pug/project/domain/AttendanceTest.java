package br.org.catolicasc.pug.project.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import br.org.catolicasc.pug.academic.domain.Student;
import br.org.catolicasc.pug.helpers.builders.ProjectBuilder;
import br.org.catolicasc.pug.helpers.builders.StudentBuilder;
import br.org.catolicasc.pug.project.domain.enums.AttendanceStatus;
import br.org.catolicasc.pug.project.domain.enums.ProjectsFieldErrorCodes;
import br.org.catolicasc.pug.shared.exceptions.BusinessRuleException;
import java.math.BigDecimal;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("Attendance Aggregate Tests")
class AttendanceTest {

  private final Project project = ProjectBuilder.aProject().build();
  private final Student student = StudentBuilder.aStudent().build();

  @Nested
  @DisplayName("Factory and Validation")
  class FactoryTests {

    @Test
    @DisplayName("Should create valid Attendance in WAITING status")
    void shouldCreateValidAttendance() {
      Attendance attendance =
          Attendance.factory(project, student, new BigDecimal("1.5"), "qr-hash-123");

      assertThat(attendance.hasFieldErrors()).isFalse();
      assertThat(attendance.getStatus()).isEqualTo(AttendanceStatus.WAITING);
    }

    @Test
    @DisplayName("Should collect errors for missing fields")
    void shouldCollectErrors() {
      Attendance attendance = Attendance.factory(null, null, null, " ");

      assertThat(attendance.hasFieldErrors()).isTrue();
      assertThat(attendance.getFieldErrors())
          .contains(
              ProjectsFieldErrorCodes.INVALID_ENROLLMENT_STUDENT_BLANK,
              ProjectsFieldErrorCodes.INVALID_ENROLLMENT_PROJECT_BLANK,
              ProjectsFieldErrorCodes.INVALID_ATTENDANCE_DURATION_INVALID,
              ProjectsFieldErrorCodes.INVALID_ATTENDANCE_QR_VALIDATION_HASH_EMPTY);
    }
  }

  @Nested
  @DisplayName("Behavior Methods")
  class BehaviorTests {

    @Test
    @DisplayName("Should transition status to PRESENT or ABSENT when validated")
    void shouldValidatePresence() {
      Attendance attendance =
          Attendance.factory(project, student, new BigDecimal("1.5"), "qr-hash-123");
      UUID staffId = UUID.randomUUID();

      Attendance updatedPresent = attendance.validatePresence(staffId, AttendanceStatus.PRESENT);
      assertThat(updatedPresent.getStatus()).isEqualTo(AttendanceStatus.PRESENT);
      assertThat(updatedPresent.getAttendanceInfo().getValidatedBy()).isEqualTo(staffId);

      Attendance updatedAbsent = attendance.validatePresence(staffId, AttendanceStatus.ABSENT);
      assertThat(updatedAbsent.getStatus()).isEqualTo(AttendanceStatus.ABSENT);
    }

    @Test
    @DisplayName("Should throw BusinessRuleException for invalid status update")
    void shouldFailOnInvalidStatus() {
      Attendance attendance =
          Attendance.factory(project, student, new BigDecimal("1.5"), "qr-hash-123");
      UUID staffId = UUID.randomUUID();

      assertThrows(
          BusinessRuleException.class,
          () -> attendance.validatePresence(staffId, AttendanceStatus.WAITING));

      assertThrows(BusinessRuleException.class, () -> attendance.validatePresence(staffId, null));
    }
  }
}
