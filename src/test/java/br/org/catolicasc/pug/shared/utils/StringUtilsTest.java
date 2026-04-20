package br.org.catolicasc.pug.shared.utils;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Locale;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

@DisplayName("StringUtils Tests")
class StringUtilsTest {

    @Nested
    @DisplayName("Method: fold")
    class FoldTests {
        @Test
        @DisplayName("Should remove diacritics, lowercase and trim")
        void shouldFoldCorrectly() {
            assertThat(StringUtils.fold("  São Paulo  ")).isEqualTo("sao paulo");
            assertThat(StringUtils.fold("João")).isEqualTo("joao");
        }

        @Test
        @DisplayName("Should return empty string for null input")
        void shouldHandleNull() {
            assertThat(StringUtils.fold(null)).isEmpty();
        }
    }

    @Nested
    @DisplayName("Method: isEmpty")
    class IsEmptyTests {
        @ParameterizedTest
        @DisplayName("Should return true for blank/null strings")
        @MethodSource("provideBlankStrings")
        void shouldReturnTrueForEmpty(String input) {
            assertThat(StringUtils.isEmpty(input)).isTrue();
        }

        @Test
        @DisplayName("Should return false for valid content")
        void shouldReturnFalseForContent() {
            assertThat(StringUtils.isEmpty("data")).isFalse();
        }

        static Stream<String> provideBlankStrings() {
            return Stream.of(null, "", "   ", "\t", "\n");
        }
    }

    @Nested
    @DisplayName("Method: isNotEmpty")
    class IsNotEmptyTests {
        @ParameterizedTest
        @DisplayName("Should return true for valid strings")
        @MethodSource("provideValidStrings")
        void shouldReturnTrueForValid(String input) {
            assertThat(StringUtils.isNotEmpty(input)).isTrue();
        }

        @ParameterizedTest
        @DisplayName("Should return false for blank/null strings")
        @MethodSource("br.org.catolicasc.pug.shared.utils.StringUtilsTest$IsEmptyTests#provideBlankStrings")
        void shouldReturnFalseForEmpty(String input) {
            assertThat(StringUtils.isNotEmpty(input)).isFalse();
        }

        static Stream<String> provideValidStrings() {
            return Stream.of("a", " data ", "123");
        }
    }

    @Nested
    @DisplayName("Method: trim")
    class TrimTests {
        @Test
        @DisplayName("Should trim whitespace")
        void shouldTrim() {
            assertThat(StringUtils.trim("  data  ")).isEqualTo("data");
        }

        @Test
        @DisplayName("Should return null for null input")
        void shouldHandleNull() {
            assertThat(StringUtils.trim(null)).isNull();
        }
    }

    @Nested
    @DisplayName("Method: toStringFormatted (OffsetDateTime)")
    class ToStringFormattedDateTimeTests {
        private final OffsetDateTime dateTime = OffsetDateTime.of(2026, 4, 20, 10, 0, 0, 0, ZoneOffset.UTC);

        @Test
        @DisplayName("Should format date time correctly")
        void shouldFormatDateTime() {
            String formatted = StringUtils.toStringFormatted(dateTime, Locale.forLanguageTag("pt-BR"));
            assertThat(formatted).contains("2026");
        }

        @Test
        @DisplayName("Should return empty string for null dateTime")
        void shouldHandleNull() {
            assertThat(StringUtils.toStringFormatted((OffsetDateTime) null, Locale.US)).isEmpty();
        }
    }

    @Nested
    @DisplayName("Method: toStringFormatted (LocalDate)")
    class ToStringFormattedDateTests {
        private final LocalDate date = LocalDate.of(2026, 4, 20);

        @Test
        @DisplayName("Should format date correctly")
        void shouldFormatDate() {
            String formatted = StringUtils.toStringFormatted(date, Locale.US);
            assertThat(formatted).contains("Apr 20, 2026");
        }

        @Test
        @DisplayName("Should return empty string for null date")
        void shouldHandleNull() {
            assertThat(StringUtils.toStringFormatted((LocalDate) null, Locale.US)).isEmpty();
        }
    }
}