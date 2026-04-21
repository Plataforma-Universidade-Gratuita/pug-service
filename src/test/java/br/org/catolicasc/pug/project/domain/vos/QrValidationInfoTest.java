package br.org.catolicasc.pug.project.domain.vos;

import static org.assertj.core.api.Assertions.assertThat;

import br.org.catolicasc.pug.project.domain.enums.ProjectsFieldErrorCodes;
import java.math.BigDecimal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("QrValidationInfo VO Tests")
class QrValidationInfoTest {
  @Test
  @DisplayName("Should create valid info")
  void shouldCreate() {
    QrValidationInfo info = QrValidationInfo.factory(new BigDecimal("1.0"), "hash-123");
    assertThat(info.hasFieldErrors()).isFalse();
  }

  @Test
  @DisplayName("Should reject invalid duration")
  void shouldReject() {
    QrValidationInfo info = QrValidationInfo.factory(BigDecimal.ZERO, " ");
    assertThat(info.hasFieldErrors()).isTrue();
    assertThat(info.getFieldErrors())
        .contains(
            ProjectsFieldErrorCodes.INVALID_ATTENDANCE_DURATION_INVALID,
            ProjectsFieldErrorCodes.INVALID_ATTENDANCE_QR_VALIDATION_HASH_EMPTY);
  }
}
