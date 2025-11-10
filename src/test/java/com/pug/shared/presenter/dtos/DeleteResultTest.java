package com.pug.shared.presenter.dtos;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Map;
import org.junit.jupiter.api.Test;

class DeleteResultTest {

  @Test
  void holds_value() {
    DeleteResult r = new DeleteResult(Map.of("deleted", 7L));
    assertEquals(Map.of("deleted", 7L), r.deleted());
  }
}
