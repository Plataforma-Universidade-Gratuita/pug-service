package br.org.catolicasc.pug.partner.service.utils;

import static org.assertj.core.api.Assertions.assertThat;

import br.org.catolicasc.pug.partner.domain.Staff;
import com.github.f4b6a3.uuid.UuidCreator;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("StaffProcessor Coverage")
class StaffProcessorTest {

  @Test
  @DisplayName("Should process create input successfully")
  void shouldProcessCreateInput() {
    UUID accountId = UuidCreator.getTimeOrderedEpoch();
    UUID entityId = UuidCreator.getTimeOrderedEpoch();

    Staff staff = StaffProcessor.processCreateInput(accountId, entityId);

    assertThat(staff.hasFieldErrors()).isFalse();
    assertThat(staff.getAccountId()).isEqualTo(accountId);
    assertThat(staff.getEntityId()).isEqualTo(entityId);
  }

  @Test
  @DisplayName("Should collect validation errors for null input")
  void shouldCollectErrors() {
    Staff staff = StaffProcessor.processCreateInput(null, null);

    assertThat(staff.hasFieldErrors()).isTrue();
  }
}
