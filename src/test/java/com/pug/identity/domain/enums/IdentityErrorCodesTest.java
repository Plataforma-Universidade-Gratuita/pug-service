package com.pug.identity.domain.enums;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.pug.shared.errors.GenericErrorCodes;
import java.util.Set;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class IdentityErrorCodesTest {

  @Test
  void implements_GenericErrorCodes() {
    assertTrue(GenericErrorCodes.class.isAssignableFrom(IdentityErrorCodes.class));
  }

  @Test
  void bundleKeys_are_not_blank_and_have_expected_prefix() {
    for (IdentityErrorCodes c : IdentityErrorCodes.values()) {
      String key = c.getBundleKey();
      assertNotNull(key);
      assertFalse(key.isBlank());
      assertTrue(key.startsWith("error.domain.identity."));
    }
  }

  @Test
  void bundleKeys_are_unique() {
    Set<String> fromValues =
        java.util.Arrays.stream(IdentityErrorCodes.values())
            .map(IdentityErrorCodes::getBundleKey)
            .collect(java.util.stream.Collectors.toSet());

    assertEquals(IdentityErrorCodes.values().length, fromValues.size());
  }

  @ParameterizedTest
  @MethodSource("expectedMappings")
  void each_constant_has_expected_bundle_key(IdentityErrorCodes constant, String expectedKey) {
    assertEquals(expectedKey, constant.getBundleKey());
  }

  private static Stream<Arguments> expectedMappings() {
    return Stream.of(
        Arguments.of(IdentityErrorCodes.INVALID_CPF, "error.domain.identity.cpf"),
        Arguments.of(
            IdentityErrorCodes.INVALID_USER_NAME_BLANK, "error.domain.identity.user.name.blank"),
        Arguments.of(
            IdentityErrorCodes.INVALID_USER_NAME_TOOLONG,
            "error.domain.identity.user.name.toolong"),
        Arguments.of(IdentityErrorCodes.INVALID_EMAIL_BLANK, "error.domain.identity.email.blank"),
        Arguments.of(
            IdentityErrorCodes.INVALID_EMAIL_TOOLONG, "error.domain.identity.email.toolong"),
        Arguments.of(IdentityErrorCodes.INVALID_EMAIL_FORMAT, "error.domain.identity.email.format"),
        Arguments.of(IdentityErrorCodes.INVALID_ACCOUNT_TYPE, "error.domain.identity.account.type"),
        Arguments.of(
            IdentityErrorCodes.INVALID_PASSWORD_HASH_TOOLONG,
            "error.domain.identity.password.hash.toolong"),
        Arguments.of(IdentityErrorCodes.INVALID_ACTIVE_NULL, "error.domain.identity.active.null"),
        Arguments.of(
            IdentityErrorCodes.INVALID_CREATED_AT_FUTURE,
            "error.domain.identity.created.at.future"),
        Arguments.of(
            IdentityErrorCodes.USER_ALREADY_EXISTS, "error.domain.identity.user.already.exists"),
        Arguments.of(IdentityErrorCodes.USER_NOT_FOUND, "error.domain.identity.user.not.found"),
        Arguments.of(
            IdentityErrorCodes.INVALID_ADMIN_USER, "error.domain.identity.admin.user.invalid"),
        Arguments.of(
            IdentityErrorCodes.ADMIN_ALREADY_EXISTS, "error.domain.identity.admin.already.exists"),
        Arguments.of(IdentityErrorCodes.ADMIN_NOT_FOUND, "error.domain.identity.admin.not.found"));
  }
}
