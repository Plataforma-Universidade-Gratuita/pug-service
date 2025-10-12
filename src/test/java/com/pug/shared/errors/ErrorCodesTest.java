package com.pug.shared.errors;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class ErrorCodesTest {

  @Test
  void mapsWellKnownCodesToBundleKeys() {
    assertEquals("error.user.duplicate_cpf", ErrorCodes.bundleKey(ErrorCodes.USER_DUPLICATE_CPF));
    assertEquals("error.validation", ErrorCodes.bundleKey(ErrorCodes.VALIDATION_ERROR));
    assertEquals("error.internal", ErrorCodes.bundleKey(ErrorCodes.INTERNAL_ERROR));
    assertEquals("error.role.not_found", ErrorCodes.bundleKey(ErrorCodes.ROLE_NOT_FOUND));
    assertEquals("error.city.not_found", ErrorCodes.bundleKey(ErrorCodes.CITY_NOT_FOUND));
    assertEquals(
        "error.entity.duplicate_cnpj", ErrorCodes.bundleKey(ErrorCodes.ENTITY_DUPLICATE_CNPJ));
    assertEquals("error.course.not_found", ErrorCodes.bundleKey(ErrorCodes.COURSE_NOT_FOUND));
    assertEquals("error.student.not_found", ErrorCodes.bundleKey(ErrorCodes.STUDENT_NOT_FOUND));
  }

  @Test
  void unknownCodeFallsBackToSameCode() {
    assertEquals("SOME_RANDOM", ErrorCodes.bundleKey("SOME_RANDOM"));
  }
}
