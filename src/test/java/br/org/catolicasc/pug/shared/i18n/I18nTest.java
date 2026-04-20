package br.org.catolicasc.pug.shared.i18n;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Locale;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("I18n Service Tests")
class I18nTest {

  private I18n i18n;

  @BeforeEach
  void setup() {
    i18n = new I18n();
  }

  @Nested
  @DisplayName("Method: translation")
  class TranslationTests {

    @Test
    @DisplayName("Should translate correctly for pt-BR")
    void shouldTranslatePtBr() {
      String result = i18n.translation("hello", Locale.forLanguageTag("pt-BR"), "Mateus");
      assertThat(result).isEqualTo("Test Olá, Mateus!");
    }

    @Test
    @DisplayName("Should translate correctly for en-US")
    void shouldTranslateEnUs() {
      String result = i18n.translation("hello", Locale.US, "Mateus");
      assertThat(result).isEqualTo("Test Hello, Mateus!");
    }

    @Test
    @DisplayName("Should return raw key if translation is missing")
    void shouldHandleMissingKey() {
      String missingKey = "non.existent.key";
      String result = i18n.translation(missingKey, Locale.US);
      assertThat(result).isEqualTo(missingKey);
    }

    @Test
    @DisplayName("Should fallback to default locale if locale is null")
    void shouldHandleNullLocale() {
      String result = i18n.translation("hello", null, "User");
      assertThat(result).isNotNull();
    }
  }
}
