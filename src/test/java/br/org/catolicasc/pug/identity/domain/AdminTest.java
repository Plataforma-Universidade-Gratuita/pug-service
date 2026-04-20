package br.org.catolicasc.pug.identity.domain;

import br.org.catolicasc.pug.identity.domain.enums.IdentityFieldErrorCodes;
import br.org.catolicasc.pug.shared.domain.enums.Campi;
import br.org.catolicasc.pug.shared.domain.enums.SharedFieldErrorCodes;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Admin Aggregate Tests")
class AdminTest {

    @Test
    @DisplayName("Should create valid Admin")
    void shouldCreateAdmin() {
        Admin admin = Admin.factory(UUID.randomUUID(), Campi.JARAGUA_DO_SUL);
        assertThat(admin.hasFieldErrors()).isFalse();
    }

    @Test
    @DisplayName("Should collect errors for Admin creation")
    void shouldCollectErrors() {
        Admin admin = Admin.factory(null, null);

        assertThat(admin.hasFieldErrors()).isTrue();
        assertThat(admin.getFieldErrors()).contains(
                IdentityFieldErrorCodes.INVALID_ACCOUNT_ID_BLANK,
                SharedFieldErrorCodes.INVALID_CAMPUS_BLANK
        );
    }
}