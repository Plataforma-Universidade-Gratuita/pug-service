package com.pug.shared.domain.enums;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class AccountTypeTest {

  @Test
  void enum_contains_expected_values() {
    assertArrayEquals(
        new AccountType[] {AccountType.ADMIN, AccountType.PARTNER, AccountType.STUDENT},
        AccountType.values());
  }

  @Test
  void valueOf_works() {
    assertEquals(AccountType.ADMIN, AccountType.valueOf("ADMIN"));
    assertEquals(AccountType.PARTNER, AccountType.valueOf("PARTNER"));
    assertEquals(AccountType.STUDENT, AccountType.valueOf("STUDENT"));
  }
}
