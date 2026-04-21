package br.org.catolicasc.pug.project.domain.vos;

import static org.assertj.core.api.Assertions.assertThat;

import br.org.catolicasc.pug.project.domain.enums.ProjectsFieldErrorCodes;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("AttendanceInfo VO Tests")
class AttendanceInfoTest {
  @Test
  @DisplayName("Should create valid info")
  void shouldCreate() {
    AttendanceInfo info = AttendanceInfo.factory(UUID.randomUUID(), OffsetDateTime.now());
    assertThat(info.hasFieldErrors()).isFalse();
  }

  @Test
  @DisplayName("Should reject mismatched validator/timestamp")
  void shouldRejectMismatched() {
    AttendanceInfo info = AttendanceInfo.factory(UUID.randomUUID(), null);
    assertThat(info.hasFieldErrors()).isTrue();
    assertThat(info.getFieldErrors())
        .contains(ProjectsFieldErrorCodes.INVALID_ATTENDANCE_STATUS_BLANK);
  }
}
