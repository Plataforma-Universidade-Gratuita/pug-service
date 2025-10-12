package com.pug.shared.i18n;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Locale;
import org.junit.jupiter.api.Test;

class I18nTest {

  @Test
  void enUS_bundleAndFormatting() {
    Locale prev = Locale.getDefault();
    try {
      Locale.setDefault(Locale.US); // en_US
      var i18n = new I18n();
      assertEquals("Internal error", i18n.t("error.internal"));
      assertEquals("Validation error", i18n.t("error.validation"));
      assertEquals("Hello Mateus", i18n.t("greet", "Mateus"));
      assertEquals("unknown.key", i18n.t("unknown.key"));
    } finally {
      Locale.setDefault(prev);
    }
  }

  @Test
  void ptBR_bundleAndFormatting() {
    Locale prev = Locale.getDefault();
    try {
      Locale.setDefault(new Locale("pt", "BR")); // pt_BR
      var i18n = new I18n();
      assertEquals("Erro interno", i18n.t("error.internal"));
      assertEquals("Erro de validação", i18n.t("error.validation"));
      assertEquals("Olá Mateus", i18n.t("greet", "Mateus"));
      assertEquals("unknown.key", i18n.t("unknown.key"));
    } finally {
      Locale.setDefault(prev);
    }
  }
}
