package com.pug.shared.presenter;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.pug.shared.utils.PresenterUtils;
import java.util.List;
import java.util.Locale;
import org.junit.jupiter.api.Test;

public class PresenterUtilsTest {

  @Test
  void nullList_fallsBackToPtBR() {
    Locale picked = PresenterUtils.pickLocale(null);
    assertEquals(Locale.forLanguageTag("pt-BR"), picked);
  }

  @Test
  void emptyList_fallsBackToPtBR() {
    Locale picked = PresenterUtils.pickLocale(List.of());
    assertEquals(Locale.forLanguageTag("pt-BR"), picked);
  }

  @Test
  void returnsFirstOfAcceptable() {
    Locale picked = PresenterUtils.pickLocale(List.of(Locale.US, Locale.forLanguageTag("pt-BR")));
    assertEquals(Locale.US, picked);
  }

  @Test
  void supportsLanguageOnlyTags() {
    Locale picked = PresenterUtils.pickLocale(List.of(Locale.forLanguageTag("es")));
    assertEquals(Locale.forLanguageTag("es"), picked);
  }
}
