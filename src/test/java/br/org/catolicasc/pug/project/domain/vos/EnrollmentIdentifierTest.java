package br.org.catolicasc.pug.project.domain.vos;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.f4b6a3.uuid.UuidCreator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("EnrollmentIdentifier VO Tests")
class EnrollmentIdentifierTest {
  @Test
  @DisplayName("Should create valid identifier")
  void shouldCreate() {
    EnrollmentIdentifier id =
        EnrollmentIdentifier.factory(
            UuidCreator.getTimeOrderedEpoch(), UuidCreator.getTimeOrderedEpoch());
    assertThat(id.hasFieldErrors()).isFalse();
  }
}
