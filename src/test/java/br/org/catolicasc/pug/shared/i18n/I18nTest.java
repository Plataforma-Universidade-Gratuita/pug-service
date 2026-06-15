package br.org.catolicasc.pug.shared.i18n;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.Locale;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("I18n Service Tests")
class I18nTest {

  private I18n i18n;
  private Locale defaultLocale;

  @BeforeEach
  void setup() {
    defaultLocale = Locale.getDefault();
    i18n = new I18n();
  }

  @AfterEach
  void tearDown() {
    Locale.setDefault(defaultLocale);
  }

  @Nested
  @DisplayName("Method: translation")
  class TranslationTests {

    @Test
    @DisplayName("Should translate correctly for pt-BR")
    void shouldTranslatePtBr() {
      String result = i18n.translation("error.validation", Locale.forLanguageTag("pt-BR"));

      assertThat(result).isEqualTo("Falha na validação dos dados informados.");
    }

    @Test
    @DisplayName("Should translate correctly for en-US")
    void shouldTranslateEnUs() {
      String result = i18n.translation("error.validation", Locale.US);

      assertThat(result).isEqualTo("Validation failed for the provided data.");
    }

    @Test
    @DisplayName("Should interpolate arguments in localized messages")
    void shouldInterpolateArguments() {
      String result = i18n.translation("academic.formerStudent.days.overdue", Locale.US, 3);

      assertThat(result).isEqualTo("Overdue (3 days)");
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
      Locale.setDefault(Locale.US);

      String result = i18n.translation("error.unauthorized", null);

      assertThat(result).isEqualTo("Invalid credentials or inactive account.");
    }

    @Test
    @DisplayName("Should use the default locale overload")
    void shouldUseDefaultLocaleOverload() {
      Locale.setDefault(Locale.forLanguageTag("pt-BR"));

      String result = i18n.translation("error.resource.not.found");

      assertThat(result).isEqualTo("O recurso solicitado não foi encontrado.");
    }

    @Test
    @DisplayName("Should return untranslated pattern when called without arguments")
    void shouldReturnPatternWhenArgumentsAreMissing() {
      String result = i18n.translation("academic.formerStudent.days.remaining", Locale.US);

      assertThat(result).isEqualTo("{0} days remaining");
    }

    @Test
    @DisplayName("Should resolve only one translated value for the same key across locales")
    void shouldResolveDistinctLocalizedValues() {
      String enResult = i18n.translation("shared.account.type.admin", Locale.US);
      String ptResult =
          i18n.translation("shared.account.type.admin", Locale.forLanguageTag("pt-BR"));

      assertThat(Arrays.asList(enResult, ptResult))
          .containsExactly("Administrator", "Administrador");
    }
  }
}
