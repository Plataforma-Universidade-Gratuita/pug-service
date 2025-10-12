package com.pug.shared.domain.time;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class DateRangeTest {

  @Test
  void ctorNullsThrow() {
    assertThrows(IllegalArgumentException.class, () -> new DateRange(null, LocalDate.now()));
    assertThrows(IllegalArgumentException.class, () -> new DateRange(LocalDate.now(), null));
  }

  @Test
  void ctorEndBeforeStartThrows() {
    var s = LocalDate.of(2024, 1, 10);
    var e = LocalDate.of(2024, 1, 9);
    assertThrows(IllegalArgumentException.class, () -> new DateRange(s, e));
  }

  @Test
  void containsIsInclusive() {
    var s = LocalDate.of(2024, 1, 1);
    var e = LocalDate.of(2024, 1, 31);
    var r = new DateRange(s, e);

    assertTrue(r.contains(s));
    assertTrue(r.contains(e));
    assertTrue(r.contains(LocalDate.of(2024, 1, 15)));
    assertFalse(r.contains(LocalDate.of(2023, 12, 31)));
    assertFalse(r.contains(LocalDate.of(2024, 2, 1)));
  }
}
