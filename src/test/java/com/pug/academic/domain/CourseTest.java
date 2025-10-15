package com.pug.academic.domain;

import static com.pug.academic.domain.AcademicErrorCodes.ACADEMIC_COURSE_NAME_REQUIRED;
import static com.pug.academic.domain.AcademicErrorCodes.ACADEMIC_COURSE_NAME_TOO_LONG;
import static com.pug.academic.domain.AcademicErrorCodes.ACADEMIC_COURSE_SCHOOL_REQUIRED;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.pug.shared.domain.exceptions.AppValidationException;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class CourseTest {
  @Test
  void validBuilds() {
    var sId = UUID.randomUUID();
    assertDoesNotThrow(() -> Course.builder().name("Computer Science").schoolId(sId).build());
  }

  @Test
  void requiresSchool() {
    var ex = assertThrows(AppValidationException.class, () -> Course.builder().name("x").build());
    assertEquals(ACADEMIC_COURSE_SCHOOL_REQUIRED, ex.code());
  }

  @Test
  void nameRequired() {
    var sId = UUID.randomUUID();
    var ex =
        assertThrows(
            AppValidationException.class, () -> Course.builder().schoolId(sId).name(" ").build());
    assertEquals(ACADEMIC_COURSE_NAME_REQUIRED, ex.code());
  }

  @Test
  void nameTooLong() {
    var sId = UUID.randomUUID();
    String longName = "X".repeat(121);
    var ex =
        assertThrows(
            AppValidationException.class,
            () -> Course.builder().schoolId(sId).name(longName).build());
    assertEquals(ACADEMIC_COURSE_NAME_TOO_LONG, ex.code());
  }
}
