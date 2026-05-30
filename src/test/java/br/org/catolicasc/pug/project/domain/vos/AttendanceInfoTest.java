package br.org.catolicasc.pug.project.domain.vos;

import static org.assertj.core.api.Assertions.assertThat;

import br.org.catolicasc.pug.project.domain.enums.ProjectsFieldErrorCodes;
import com.github.f4b6a3.uuid.UuidCreator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("AttendanceInfo VO Tests")
class AttendanceInfoTest {
  @Test
  @DisplayName("Should create valid info")
  void shouldCreate() {
    AttendanceInfo info = AttendanceInfo.factory(null, null);
    assertThat(info.hasFieldErrors()).isFalse();
  }

  @Test
  @DisplayName("Should reject mismatched validator/timestamp")
  void shouldRejectMismatched() {
    AttendanceInfo info = AttendanceInfo.factory(UuidCreator.getTimeOrderedEpoch(), null);
    assertThat(info.hasFieldErrors()).isTrue();
    assertThat(info.getFieldErrors())
        .contains(ProjectsFieldErrorCodes.INVALID_ATTENDANCE_STATUS_BLANK);
  }
}
