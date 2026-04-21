package br.org.catolicasc.pug.project.domain;

import static org.assertj.core.api.Assertions.assertThat;

import br.org.catolicasc.pug.academic.domain.Student;
import br.org.catolicasc.pug.builders.ProjectBuilder;
import br.org.catolicasc.pug.builders.StudentBuilder;
import br.org.catolicasc.pug.project.domain.enums.AttendanceStatus;
import br.org.catolicasc.pug.project.domain.enums.ProjectsFieldErrorCodes;
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
      assertThat(attendance.getQrValidationInfo().getQrValidationHash()).isEqualTo("qr-hash-123");
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
    @DisplayName("Should transition status to PRESENT when validated")
    void shouldValidatePresence() {
      Attendance attendance =
          Attendance.factory(project, student, new BigDecimal("1.5"), "qr-hash-123");

      UUID staffId = UUID.randomUUID();
      Attendance updated = attendance.validatePresence(staffId, AttendanceStatus.PRESENT);

      assertThat(updated.getStatus()).isEqualTo(AttendanceStatus.PRESENT);
      assertThat(updated.getAttendanceInfo().getValidatedBy()).isEqualTo(staffId);
      assertThat(updated.getAttendanceInfo().getValidatedAt()).isNotNull();
    }
  }
}
