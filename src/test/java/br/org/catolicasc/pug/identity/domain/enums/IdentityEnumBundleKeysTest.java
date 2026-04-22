package br.org.catolicasc.pug.identity.domain.enums;

import br.org.catolicasc.pug.helpers.EnumBundleKeyValidator;
import br.org.catolicasc.pug.shared.domain.enums.GenericCodes;
import java.util.Locale;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@DisplayName("Identity Enum Bundle Key Integrity Tests")
class IdentityEnumBundleKeysTest {

  @ParameterizedTest(name = "{0} - Locale: {1}")
  @DisplayName("Verify that all identity enum keys exist in properties files")
  @MethodSource("provideEnumsAndLocales")
  void verifyEnumKeysExist(String enumName, GenericCodes code, Locale locale) {
    EnumBundleKeyValidator.assertKeyExists(code, locale);
  }

  static Stream<Arguments> provideEnumsAndLocales() {
    return EnumBundleKeyValidator.buildArguments(
        IdentityErrorCodes.values(), IdentityFieldErrorCodes.values());
  }
}
