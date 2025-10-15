package com.pug.academic.domain;

import static com.pug.academic.domain.AcademicErrorCodes.ACADEMIC_SCHOOL_NAME_REQUIRED;
import static com.pug.academic.domain.AcademicErrorCodes.ACADEMIC_SCHOOL_NAME_TOO_LONG;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.pug.shared.domain.exceptions.AppValidationException;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class SchoolTest {

  @Test
  void validBuilds() {
    assertDoesNotThrow(() -> School.builder().id(UUID.randomUUID()).name("UFSC").build());
  }

  @Test
  void nameRequired() {
    var ex = assertThrows(AppValidationException.class, () -> School.builder().name(" ").build());
    assertEquals(ACADEMIC_SCHOOL_NAME_REQUIRED, ex.code());
  }

  @Test
  void nameTooLong() {
    String longName = "X".repeat(101);
    var ex =
        assertThrows(AppValidationException.class, () -> School.builder().name(longName).build());
    assertEquals(ACADEMIC_SCHOOL_NAME_TOO_LONG, ex.code());
  }
}
