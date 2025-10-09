package com.pug.academic.domain.counterpartHours;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.pug.academic.domain.CounterpartHours;
import com.pug.academic.domain.Student;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class CounterpartHoursValidationTest {
  static Validator validator;
  static Locale original;

  @BeforeAll
  static void boot() {
    original = Locale.getDefault();
    Locale.setDefault(Locale.ENGLISH);
    validator = Validation.buildDefaultValidatorFactory().getValidator();
  }

  @AfterAll
  static void tearDown() {
    Locale.setDefault(original);
  }

  private static Student stubStudent() {
    var s = new Student();
    s.setId(UUID.randomUUID());
    return s;
  }

  @Test
  void validPasses() {
    var h =
        CounterpartHours.builder()
            .student(stubStudent())
            .requiredHours(new BigDecimal("120.00"))
            .startDate(LocalDate.of(2025, 1, 1))
            .dueDate(LocalDate.of(2025, 12, 31))
            .build();

    assertTrue(validator.validate(h).isEmpty());
  }

  @Test
  void studentNotNull() {
    var h =
        CounterpartHours.builder()
            .student(null)
            .requiredHours(new BigDecimal("10.00"))
            .startDate(LocalDate.now())
            .dueDate(LocalDate.now())
            .build();

    var cv = one(validator.validate(h), "student");
    assertEquals("{jakarta.validation.constraints.NotNull.message}", cv.getMessageTemplate());
  }

  @Test
  void requiredHoursNotNullAndDigits() {
    var nullHours =
        CounterpartHours.builder()
            .student(stubStudent())
            .requiredHours(null)
            .startDate(LocalDate.now())
            .dueDate(LocalDate.now())
            .build();
    var cv1 = one(validator.validate(nullHours), "requiredHours");
    assertEquals("{jakarta.validation.constraints.NotNull.message}", cv1.getMessageTemplate());

    var fracTooLong =
        CounterpartHours.builder()
            .student(stubStudent())
            .requiredHours(new BigDecimal("1.234"))
            .startDate(LocalDate.now())
            .dueDate(LocalDate.now())
            .build();
    var cv2 = one(validator.validate(fracTooLong), "requiredHours");
    assertEquals("{jakarta.validation.constraints.Digits.message}", cv2.getMessageTemplate());

    var intTooLong =
        CounterpartHours.builder()
            .student(stubStudent())
            .requiredHours(new BigDecimal("12345.00"))
            .startDate(LocalDate.now())
            .dueDate(LocalDate.now())
            .build();
    var cv3 = one(validator.validate(intTooLong), "requiredHours");
    assertEquals("{jakarta.validation.constraints.Digits.message}", cv3.getMessageTemplate());
  }

  @Test
  void datesNotNull() {
    var h =
        CounterpartHours.builder()
            .student(stubStudent())
            .requiredHours(new BigDecimal("10.00"))
            .startDate(null)
            .dueDate(null)
            .build();

    var props = validator.validate(h).stream().map(cv -> cv.getPropertyPath().toString()).toList();

    assertTrue(props.contains("startDate"));
    assertTrue(props.contains("dueDate"));
  }

  private static ConstraintViolation<CounterpartHours> one(
      Set<ConstraintViolation<CounterpartHours>> v, String prop) {
    List<ConstraintViolation<CounterpartHours>> list =
        v.stream().filter(cv -> cv.getPropertyPath().toString().equals(prop)).toList();
    assertEquals(1, list.size());
    return list.getFirst();
  }
}
