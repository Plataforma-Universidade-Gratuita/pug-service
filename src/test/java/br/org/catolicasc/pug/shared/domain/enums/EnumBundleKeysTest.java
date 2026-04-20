package br.org.catolicasc.pug.shared.domain.enums;

import br.org.catolicasc.pug.shared.i18n.I18n;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Locale;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Enum Bundle Key Integrity Tests")
class EnumBundleKeysTest {

    private final I18n i18n = new I18n();

    @ParameterizedTest(name = "{0} - Locale: {1}")
    @DisplayName("Verify that all enum keys exist in properties files")
    @MethodSource("provideEnumsAndLocales")
    void verifyEnumKeysExist(String enumName, GenericCodes code, Locale locale) {
        String key = code.getBundleKey();
        String translation = i18n.translation(key, locale);

        assertThat(translation)
                .as("Key '%s' not found for %s in locale %s", key, enumName, locale)
                .isNotEqualTo(key);
    }

    static Stream<Arguments> provideEnumsAndLocales() {
        Locale ptBr = Locale.forLanguageTag("pt-BR");
        Locale enUs = Locale.forLanguageTag("en-US");

        return Stream.of(
                        AccountType.values(),
                        Campi.values(),
                        SharedErrorCodes.values(),
                        SharedFieldErrorCodes.values()
                )
                .flatMap(values -> Stream.of(values))
                .flatMap(code -> Stream.of(
                        Arguments.of(code.getClass().getSimpleName() + "." + code.toString(), code, ptBr),
                        Arguments.of(code.getClass().getSimpleName() + "." + code.toString(), code, enUs)
                ));
    }
}