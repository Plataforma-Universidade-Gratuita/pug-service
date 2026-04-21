package br.org.catolicasc.pug.project.infra;

import static org.assertj.core.api.Assertions.assertThat;

import br.org.catolicasc.pug.helpers.builders.AttendanceBuilder;
import br.org.catolicasc.pug.project.domain.Attendance;
import br.org.catolicasc.pug.project.infra.persistence.AttendanceEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("AttendanceMapper Tests")
class AttendanceMapperTest {

  @Test
  @DisplayName("Should perform round-trip mapping for Attendance")
  void shouldPerformRoundTrip() {
    Attendance attendance = AttendanceBuilder.anAttendance().build();

    AttendanceEntity entity = AttendanceMapper.toEntity(attendance);
    Attendance mapped = AttendanceMapper.toDomain(entity);

    assertThat(mapped.getId()).isEqualTo(attendance.getId());
    assertThat(mapped.getStatus()).isEqualTo(attendance.getStatus());
    assertThat(mapped.getQrValidationInfo().getQrValidationHash())
        .isEqualTo(attendance.getQrValidationInfo().getQrValidationHash());
  }
}
