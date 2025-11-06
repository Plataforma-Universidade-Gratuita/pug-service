package com.pug.shared.i18n;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.quarkus.test.junit.QuarkusTest;
import java.util.Locale;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class I18nTest {

  @Test
  public void testTranslationKeyFoundEnUS() {
    Locale.setDefault(Locale.forLanguageTag("en-US"));
    I18n i18n = new I18n();
    String translated = i18n.translation("hello", "John");

    assertEquals(
        "Test Hello, John!",
        translated,
        "The translation should come from the test-specific properties file for en-US.");
  }

  @Test
  public void testTranslationKeyFoundPtBR() {
    Locale.setDefault(Locale.forLanguageTag("pt-BR"));
    I18n i18n = new I18n();
    String translated = i18n.translation("hello", "John");

    assertEquals(
        "Test Olá, John!",
        translated,
        "The translation should come from the test-specific properties file for pt-BR.");
  }

  @Test
  public void testMissingTranslationKey() {
    I18n i18n = new I18n();
    String translated = i18n.translation("nonexistent");

    assertEquals(
        "nonexistent",
        translated,
        "The missing key should be handled by the test-specific message.");
  }
}
