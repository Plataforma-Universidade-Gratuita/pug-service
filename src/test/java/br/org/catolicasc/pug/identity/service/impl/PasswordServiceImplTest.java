package br.org.catolicasc.pug.identity.service.impl;

import static org.assertj.core.api.Assertions.assertThat;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@QuarkusTest
@DisplayName("PasswordServiceImpl Coverage")
class PasswordServiceImplTest {

  @Inject PasswordServiceImpl passwordService;

  @Test
  @DisplayName("Should hash and verify password correctly")
  void shouldHashAndVerify() {
    String raw = "mySecretPassword";

    String hash = passwordService.hash(raw);
    assertThat(hash).isNotEqualTo(raw);

    boolean matches = passwordService.verify(hash, raw);
    assertThat(matches).isTrue();
  }

  @Test
  @DisplayName("Should return false for incorrect password")
  void shouldFailVerification() {
    String raw = "mySecretPassword";
    String wrong = "wrongPassword";
    String hash = passwordService.hash(raw);

    boolean matches = passwordService.verify(hash, wrong);
    assertThat(matches).isFalse();
  }
}
