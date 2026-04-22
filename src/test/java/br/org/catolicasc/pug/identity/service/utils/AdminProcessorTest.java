package br.org.catolicasc.pug.identity.service.utils;

import static org.assertj.core.api.Assertions.assertThat;

import br.org.catolicasc.pug.identity.domain.Admin;
import br.org.catolicasc.pug.shared.domain.enums.Campi;
import com.github.f4b6a3.uuid.UuidCreator;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("AdminProcessor Tests")
class AdminProcessorTest {

  @Test
  @DisplayName("Should process create input successfully")
  void shouldProcessCreateInput() {
    UUID accountId = UuidCreator.getTimeOrderedEpoch();
    Admin admin = AdminProcessor.processCreateInput(accountId, Campi.JOINVILLE);

    assertThat(admin.hasFieldErrors()).isFalse();
    assertThat(admin.getAccountId()).isEqualTo(accountId);
    assertThat(admin.getCampus()).isEqualTo(Campi.JOINVILLE);
  }

  @Test
  @DisplayName("Should mutate campus correctly via update")
  void shouldUpdateAdmin() {
    Admin existing = Admin.factory(UuidCreator.getTimeOrderedEpoch(), Campi.JARAGUA_DO_SUL);

    Admin updated = AdminProcessor.processUpdateInput(existing, Campi.JOINVILLE);

    assertThat(updated.getCampus()).isEqualTo(Campi.JOINVILLE);
    assertThat(updated.getAccountId()).isEqualTo(existing.getAccountId());
  }

  @Test
  @DisplayName("Should skip update when new campus is null")
  void shouldSkipUpdateIfNull() {
    Admin existing = Admin.factory(UuidCreator.getTimeOrderedEpoch(), Campi.JARAGUA_DO_SUL);

    Admin updated = AdminProcessor.processUpdateInput(existing, null);

    assertThat(updated).isEqualTo(existing);
  }
}
