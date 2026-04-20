package br.org.catolicasc.pug.shared.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("PresenterUtils Tests")
class PresenterUtilsTest {

    @Nested
    @DisplayName("Method: pickLocale")
    class PickLocaleTests {

        @Test
        @DisplayName("Should return the first locale when list is provided")
        void shouldReturnFirstLocale() {
            List<Locale> locales = List.of(Locale.US, Locale.FRANCE);
            Locale result = PresenterUtils.pickLocale(locales);

            assertThat(result).isEqualTo(Locale.US);
        }

        @Test
        @DisplayName("Should return pt-BR as default when list is null")
        void shouldReturnDefaultForNull() {
            Locale result = PresenterUtils.pickLocale(null);

            assertThat(result.toLanguageTag()).isEqualTo("pt-BR");
        }

        @Test
        @DisplayName("Should return pt-BR as default when list is empty")
        void shouldReturnDefaultForEmpty() {
            Locale result = PresenterUtils.pickLocale(Collections.emptyList());

            assertThat(result.toLanguageTag()).isEqualTo("pt-BR");
        }
    }
}