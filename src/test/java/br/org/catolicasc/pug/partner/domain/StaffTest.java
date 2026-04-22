package br.org.catolicasc.pug.partner.domain;

import static org.assertj.core.api.Assertions.assertThat;

import br.org.catolicasc.pug.partner.domain.enums.PartnerFieldErrorCodes;
import com.github.f4b6a3.uuid.UuidCreator;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Staff Aggregate Tests")
class StaffTest {

  @Test
  @DisplayName("Should create valid Staff assignment")
  void shouldCreateStaff() {
    UUID accountId = UuidCreator.getTimeOrderedEpoch();
    UUID entityId = UuidCreator.getTimeOrderedEpoch();

    Staff staff = Staff.factory(accountId, entityId);

    assertThat(staff.hasFieldErrors()).isFalse();
    assertThat(staff.getAccountId()).isEqualTo(accountId);
    assertThat(staff.getEntityId()).isEqualTo(entityId);
  }

  @Test
  @DisplayName("Should collect errors when IDs are missing")
  void shouldCollectValidationErrors() {
    Staff staff = Staff.factory(null, null);

    assertThat(staff.hasFieldErrors()).isTrue();
    assertThat(staff.getFieldErrors())
        .contains(
            PartnerFieldErrorCodes.INVALID_ACCOUNT_ID_BLANK,
            PartnerFieldErrorCodes.INVALID_ENTITY_ID_BLANK);
  }
}
