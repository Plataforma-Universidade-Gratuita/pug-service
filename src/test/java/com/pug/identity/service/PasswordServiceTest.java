package com.pug.identity.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.quarkus.elytron.security.common.BcryptUtil;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

@QuarkusTest
class PasswordServiceTest {

  @Inject PasswordService service;

  @Test
  void hash_and_verify_roundtrip_ok() {
    String raw = "Str0ng#Pass";
    String hash = service.hash(raw);

    assertNotNull(hash);
    assertTrue(service.verify(hash, raw));
  }

  @Test
  void verify_wrong_password_returns_false() {
    String raw = "Str0ng#Pass";
    String hash = service.hash(raw);
    assertFalse(service.verify(hash, "wrong"));
  }

  @Test
  void hash_is_salted_hashes_differ_but_both_match() {
    String raw = "sameInput";
    String h1 = service.hash(raw);
    String h2 = service.hash(raw);

    assertNotEquals(h1, h2);
    assertTrue(service.verify(h1, raw));
    assertTrue(service.verify(h2, raw));
  }

  @Test
  void hash_requires_pepper() {
    String raw = "peppered";
    String hash = service.hash(raw);

    assertFalse(BcryptUtil.matches(raw, hash));
    assertFalse(BcryptUtil.matches(raw + "wrong-pepper", hash));
  }
}
