package br.org.catolicasc.pug.helpers;

import static org.assertj.core.api.Assertions.assertThat;

import br.org.catolicasc.pug.shared.domain.enums.GenericCodes;
import br.org.catolicasc.pug.shared.i18n.I18n;
import java.util.Locale;
import java.util.stream.Stream;
import org.junit.jupiter.params.provider.Arguments;

/**
 * Utility for enum bundle key integrity tests. Generates parameterized test arguments that verify
 * every {@link GenericCodes} constant has a translation in both pt-BR and en-US property files.
 *
 * <p>Usage in a test class:
 *
 * <pre>{@code
 * static Stream<Arguments> provideEnumsAndLocales() {
 *   return EnumBundleKeyValidator.buildArguments(
 *       MyErrorCodes.values(),
 *       MyFieldErrorCodes.values());
 * }
 * }</pre>
 */
public final class EnumBundleKeyValidator {

  private static final Locale PT_BR = Locale.forLanguageTag("pt-BR");
  private static final Locale EN_US = Locale.forLanguageTag("en-US");
  private static final I18n I18N = new I18n();

  private EnumBundleKeyValidator() {}

  /**
   * Builds a stream of JUnit {@link Arguments} for parameterized tests. Each enum constant is
   * paired with both pt-BR and en-US locales.
   *
   * @param enumArrays one or more arrays of {@link GenericCodes} enum values
   * @return stream of (displayName, code, locale) arguments
   */
  public static Stream<Arguments> buildArguments(GenericCodes[]... enumArrays) {
    return Stream.of(enumArrays)
        .flatMap(Stream::of)
        .flatMap(
            code ->
                Stream.of(
                    Arguments.of(
                        code.getClass().getSimpleName() + "." + code.toString(), code, PT_BR),
                    Arguments.of(
                        code.getClass().getSimpleName() + "." + code.toString(), code, EN_US)));
  }

  /**
   * Asserts that a given {@link GenericCodes} constant has a valid translation (not equal to the
   * raw key) for the given locale.
   */
  public static void assertKeyExists(GenericCodes code, Locale locale) {
    String key = code.getBundleKey();
    String translation = I18N.translation(key, locale);

    assertThat(translation)
        .as("Key '%s' not found for %s in locale %s", key, code, locale)
        .isNotEqualTo(key);
  }
}
