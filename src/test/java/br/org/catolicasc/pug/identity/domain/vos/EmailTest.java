package br.org.catolicasc.pug.identity.domain.vos;

import br.org.catolicasc.pug.identity.domain.enums.IdentityFieldErrorCodes;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Email Value Object Tests")
class EmailTest {

    @Nested
    @DisplayName("Factory and Validation")
    class FactoryTests {

        @Test
        @DisplayName("Should create valid Email and normalize it")
        void shouldCreateAndNormalize() {
            Email email = Email.factory("  MATEUS@PUG.COM  ");
            assertThat(email.hasFieldErrors()).isFalse();
            assertThat(email.getValue()).isEqualTo("mateus@pug.com");
        }

        @Test
        @DisplayName("Should reject invalid email formats")
        void shouldRejectInvalidFormat() {
            Email email = Email.factory("invalid-email");
            assertThat(email.hasFieldErrors()).isTrue();
            assertThat(email.getFieldErrors()).contains(IdentityFieldErrorCodes.INVALID_EMAIL_FORMAT);
        }

        @Test
        @DisplayName("Should reject blank/null emails")
        void shouldRejectEmpty() {
            Email email = Email.factory(null);
            assertThat(email.hasFieldErrors()).isTrue();
            assertThat(email.getFieldErrors()).contains(IdentityFieldErrorCodes.INVALID_EMAIL_BLANK);
        }
    }
}