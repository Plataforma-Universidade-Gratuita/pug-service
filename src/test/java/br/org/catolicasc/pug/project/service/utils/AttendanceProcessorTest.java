package br.org.catolicasc.pug.project.service.utils;

import static org.assertj.core.api.Assertions.assertThat;

import br.org.catolicasc.pug.helpers.builders.AttendanceBuilder;
import br.org.catolicasc.pug.helpers.builders.ProjectBuilder;
import br.org.catolicasc.pug.helpers.builders.StudentBuilder;
import br.org.catolicasc.pug.project.domain.Attendance;
import br.org.catolicasc.pug.project.domain.enums.AttendanceStatus;
import java.math.BigDecimal;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("AttendanceProcessor Tests")
class AttendanceProcessorTest {

  @Test
  @DisplayName("Should create attendance with valid inputs")
  void processCreateInputValid() {
    var project = ProjectBuilder.aProject().build();
    var student = StudentBuilder.aStudent().build();

    Attendance attendance =
        AttendanceProcessor.processCreateInput(
            project, student, new BigDecimal("2.00"), "hash-test-123");

    assertThat(attendance).isNotNull();
    assertThat(attendance.getEnrollmentIdentifier().getProjectId()).isEqualTo(project.getId());
    assertThat(attendance.getEnrollmentIdentifier().getStudentId())
        .isEqualTo(student.getAccountId());
    assertThat(attendance.getQrValidationInfo().getDuration())
        .isEqualByComparingTo(new BigDecimal("2.00"));
    assertThat(attendance.getQrValidationInfo().getQrValidationHash()).isEqualTo("hash-test-123");
    assertThat(attendance.getStatus()).isEqualTo(AttendanceStatus.WAITING);
    assertThat(attendance.hasFieldErrors()).isFalse();
  }

  @Test
  @DisplayName("Should process validation input to PRESENT")
  void processValidationInputPresent() {
    Attendance existing = AttendanceBuilder.anAttendance().build();
    UUID validatorId = UUID.randomUUID();

    Attendance validated =
        AttendanceProcessor.processValidationInput(existing, validatorId, AttendanceStatus.PRESENT);

    assertThat(validated.getStatus()).isEqualTo(AttendanceStatus.PRESENT);
    assertThat(validated.getAttendanceInfo().getValidatedBy()).isEqualTo(validatorId);
    assertThat(validated.getAttendanceInfo().getValidatedAt()).isNotNull();
  }

  @Test
  @DisplayName("Should process validation input to ABSENT")
  void processValidationInputAbsent() {
    Attendance existing = AttendanceBuilder.anAttendance().build();
    UUID validatorId = UUID.randomUUID();

    Attendance validated =
        AttendanceProcessor.processValidationInput(existing, validatorId, AttendanceStatus.ABSENT);

    assertThat(validated.getStatus()).isEqualTo(AttendanceStatus.ABSENT);
    assertThat(validated.getAttendanceInfo().getValidatedBy()).isEqualTo(validatorId);
  }
}
