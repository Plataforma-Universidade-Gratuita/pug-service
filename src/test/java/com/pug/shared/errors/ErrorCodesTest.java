package com.pug.shared.errors;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class ErrorCodesTest {

  @Test
  void mapsWellKnownCodesToBundleKeys() {
    assertEquals("error.validation", ErrorCodes.bundleKey(ErrorCodes.VALIDATION_ERROR));
    assertEquals("error.internal", ErrorCodes.bundleKey(ErrorCodes.INTERNAL_ERROR));
  }

  @Test
  void unknownCodeFallsBackToSameCode() {
    assertEquals("SOME_RANDOM", ErrorCodes.bundleKey("SOME_RANDOM"));
  }
}
