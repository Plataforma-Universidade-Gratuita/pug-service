package com.pug.partner.domain.enums;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.pug.shared.errors.GenericErrorCodes;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class PartnerErrorCodesTest {

  @Test
  void implementsGenericErrorCodes() {
    assertTrue(GenericErrorCodes.class.isAssignableFrom(PartnerErrorCodes.class));
  }

  @Test
  void bundleKeysAreNonBlankUniqueAndWellFormed() {
    Set<String> seen = new HashSet<>();
    for (PartnerErrorCodes code : PartnerErrorCodes.values()) {
      String key = code.getBundleKey();
      assertNotNull(key, code.name() + " bundleKey is null");
      assertFalse(key.isBlank(), code.name() + " bundleKey is blank");
      assertTrue(
          key.startsWith("error.domain.partner."),
          code.name() + " bundleKey must start with error.domain.partner.");
      assertTrue(seen.add(key), "Duplicated bundleKey: " + key);
    }
  }

  @Test
  void containsExpectedConstants() {
    Set<PartnerErrorCodes> expected =
        EnumSet.of(
            PartnerErrorCodes.INVALID_CNPJ,
            PartnerErrorCodes.INVALID_NAME_BLANK,
            PartnerErrorCodes.INVALID_NAME_TOOLONG,
            PartnerErrorCodes.INVALID_CITY,
            PartnerErrorCodes.INVALID_ADDRESS_TOOLONG,
            PartnerErrorCodes.INVALID_STAFF_USER,
            PartnerErrorCodes.INVALID_STAFF_ENTITY,
            PartnerErrorCodes.ENTITY_ALREADY_EXISTS,
            PartnerErrorCodes.ENTITY_NOT_FOUND,
            PartnerErrorCodes.STAFF_ALREADY_EXISTS,
            PartnerErrorCodes.STAFF_NOT_FOUND);

    assertTrue(
        EnumSet.allOf(PartnerErrorCodes.class).containsAll(expected),
        "Enum is missing one or more expected constants");
  }
}
