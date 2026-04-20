package br.org.catolicasc.pug.identity.domain;

import br.org.catolicasc.pug.identity.domain.enums.IdentityFieldErrorCodes;
import br.org.catolicasc.pug.identity.domain.vos.Email;
import br.org.catolicasc.pug.shared.domain.enums.AccountType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Account Aggregate Tests")
class AccountTest {

    @Test
    @DisplayName("Should collect errors for missing fields")
    void shouldCollectErrors() {
        Account account = Account.factory(null, null, null, "");

        assertThat(account.hasFieldErrors()).isTrue();
        assertThat(account.getFieldErrors()).contains(
                IdentityFieldErrorCodes.INVALID_USER_ID_BLANK,
                IdentityFieldErrorCodes.INVALID_EMAIL_BLANK,
                IdentityFieldErrorCodes.INVALID_ACCOUNT_TYPE_BLANK,
                IdentityFieldErrorCodes.INVALID_PASSWORD_HASH_BLANK
        );
    }

    @Test
    @DisplayName("Should deactivate account correctly")
    void shouldDeactivate() throws InterruptedException {
        Account acc = Account.factory(UUID.randomUUID(), Email.factory("test@pug.com"), AccountType.ADMIN, "secret");
        Thread.sleep(1);
        Account deactivated = acc.deactivate();

        assertThat(deactivated.getActive()).isFalse();
        assertThat(deactivated.getAuditInfo().getUpdatedAt()).isAfter(acc.getAuditInfo().getCreatedAt());
    }
}