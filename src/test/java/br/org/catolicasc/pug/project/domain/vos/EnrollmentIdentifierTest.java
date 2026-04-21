package br.org.catolicasc.pug.project.domain.vos;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("EnrollmentIdentifier VO Tests")
class EnrollmentIdentifierTest {
  @Test
  @DisplayName("Should create valid identifier")
  void shouldCreate() {
    EnrollmentIdentifier id = EnrollmentIdentifier.factory(UUID.randomUUID(), UUID.randomUUID());
    assertThat(id.hasFieldErrors()).isFalse();
  }
}
