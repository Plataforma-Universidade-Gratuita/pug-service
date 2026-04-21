package br.org.catolicasc.pug.project.domain.vos;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("ProjectInfo VO Tests")
class ProjectInfoTest {
  @Test
  @DisplayName("Should create valid info")
  void shouldCreate() {
    ProjectInfo info =
        ProjectInfo.factory(UUID.randomUUID(), 10, new BigDecimal("40"), BigDecimal.ZERO);
    assertThat(info.hasFieldErrors()).isFalse();
  }
}
