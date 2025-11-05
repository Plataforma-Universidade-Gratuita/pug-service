package com.pug.shared.i18n;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
public class I18nTest {

    @Test
    public void testTranslationKeyFound() {
        I18n i18n = new I18n();
        String translated = i18n.t("hello", "John");

        assertEquals("Test Hello, John!", translated, "The translation should come from the test-specific properties file.");
    }

    @Test
    public void testMissingTranslationKey() {
        I18n i18n = new I18n();
        String translated = i18n.t("nonexistent");

        assertEquals("nonexistent", translated, "The missing key should be handled by the test-specific message.");
    }
}
