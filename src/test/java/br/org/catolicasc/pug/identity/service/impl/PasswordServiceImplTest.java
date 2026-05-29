package br.org.catolicasc.pug.identity.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import br.org.catolicasc.pug.shared.exceptions.BusinessRuleException;
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

  @Test
  @DisplayName("Should report whether a password hash is configured")
  void shouldReportConfiguredPassword() {
    assertThat(passwordService.isConfigured("hash")).isTrue();
    assertThat(passwordService.isConfigured(" ")).isFalse();
    assertThat(passwordService.isConfigured(null)).isFalse();
  }

  @Test
  @DisplayName("Should accept a strong password")
  void shouldAcceptStrongPassword() {
    assertDoesNotThrow(() -> passwordService.validateStrength("StrongPass1!"));
  }

  @Test
  @DisplayName("Should reject a weak password")
  void shouldRejectWeakPassword() {
    assertThrows(BusinessRuleException.class, () -> passwordService.validateStrength("weakpass"));
  }
}
