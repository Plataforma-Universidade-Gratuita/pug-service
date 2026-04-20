package br.org.catolicasc.pug.identity.domain.enums;

import br.org.catolicasc.pug.shared.domain.enums.GenericCodes;
import br.org.catolicasc.pug.shared.i18n.I18n;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Locale;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Identity Enum Bundle Key Integrity Tests")
class IdentityEnumBundleKeysTest {

    private final I18n i18n = new I18n();

    @ParameterizedTest(name = "{0} - Locale: {1}")
    @DisplayName("Verify that all identity enum keys exist in properties files")
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
                        mapEnum(IdentityErrorCodes.values(), "IdentityErrorCodes"),
                        mapEnum(IdentityFieldErrorCodes.values(), "IdentityFieldErrorCodes")
                )
                .flatMap(values -> values)
                .flatMap(args -> Stream.of(
                        Arguments.of(args.get()[0], args.get()[1], ptBr),
                        Arguments.of(args.get()[0], args.get()[1], enUs)
                ));
    }

    private static Stream<Arguments> mapEnum(GenericCodes[] values, String name) {
        return Stream.of(values).map(e -> Arguments.of(name + "." + e.toString(), e));
    }
}