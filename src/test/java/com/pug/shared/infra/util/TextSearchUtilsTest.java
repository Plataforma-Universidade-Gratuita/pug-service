package com.pug.shared.infra.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class TextSearchUtilsTest {

  @Test
  void normalizeHandlesNullAndAccents() {
    assertNull(TextSearchUtils.normalize(null));
    assertEquals("florianopolis", TextSearchUtils.normalize("Florianópolis"));
    assertEquals("acao", TextSearchUtils.normalize("Ação"));
    assertEquals("user@example.com", TextSearchUtils.normalize("User@Example.Com"));
  }

  @Test
  void likeParamWrapsAndNormalizes() {
    assertEquals("%florianopolis%", TextSearchUtils.likeParam("Florianópolis"));
    assertEquals("%abc%", TextSearchUtils.likeParam("ABC"));
  }
}
