package br.org.catolicasc.pug.project.domain.vos;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("EnrollmentInfo VO Tests")
class EnrollmentInfoTest {
  @Test
  @DisplayName("Should create valid info")
  void shouldCreate() {
    EnrollmentInfo info = EnrollmentInfo.factory();
    assertThat(info.hasFieldErrors()).isFalse();
  }
}
