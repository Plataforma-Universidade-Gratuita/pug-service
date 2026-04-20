package br.org.catolicasc.pug.identity.service.utils;

import br.org.catolicasc.pug.identity.domain.Admin;
import br.org.catolicasc.pug.shared.domain.enums.Campi;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("AdminProcessor Tests")
class AdminProcessorTest {

    @Test
    @DisplayName("Should process create input successfully")
    void shouldProcessCreateInput() {
        UUID accountId = UUID.randomUUID();
        Admin admin = AdminProcessor.processCreateInput(accountId, Campi.JOINVILLE);

        assertThat(admin.hasFieldErrors()).isFalse();
        assertThat(admin.getAccountId()).isEqualTo(accountId);
        assertThat(admin.getCampus()).isEqualTo(Campi.JOINVILLE);
    }

    @Test
    @DisplayName("Should mutate campus correctly via update")
    void shouldUpdateAdmin() {
        Admin existing = Admin.factory(UUID.randomUUID(), Campi.JARAGUA_DO_SUL);

        Admin updated = AdminProcessor.processUpdateInput(existing, Campi.JOINVILLE);

        assertThat(updated.getCampus()).isEqualTo(Campi.JOINVILLE);
        assertThat(updated.getAccountId()).isEqualTo(existing.getAccountId());
    }

    @Test
    @DisplayName("Should skip update when new campus is null")
    void shouldSkipUpdateIfNull() {
        Admin existing = Admin.factory(UUID.randomUUID(), Campi.JARAGUA_DO_SUL);

        Admin updated = AdminProcessor.processUpdateInput(existing, null);

        assertThat(updated).isEqualTo(existing);
    }
}