package com.pug.shared.infra.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.Test;

class PageTest {

  @Test
  void pagesExactDivision() {
    var p = new Page<>(List.of(1, 2), 100, 0, 10);
    assertEquals(10, p.pages());
  }

  @Test
  void pagesRoundsUpOnRemainder() {
    var p = new Page<>(List.of(1, 2), 101, 0, 10);
    assertEquals(11, p.pages());
  }
}
