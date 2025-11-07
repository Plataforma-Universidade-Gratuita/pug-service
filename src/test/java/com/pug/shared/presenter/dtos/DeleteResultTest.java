package com.pug.shared.presenter.dtos;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class DeleteResultTest {

  @Test
  void holds_value() {
    DeleteResult r = new DeleteResult(7L);
    assertEquals(7L, r.deleted());
  }
}
