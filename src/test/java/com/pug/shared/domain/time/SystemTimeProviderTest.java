package com.pug.shared.domain.time;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import org.junit.jupiter.api.Test;

class SystemTimeProviderTest {

  @Test
  void clockIsSystemUtcAndStable() {
    var tp = new SystemTimeProvider();
    Clock c1 = tp.clock();
    Clock c2 = tp.clock();
    assertNotNull(c1);
    assertSame(c1, c2);
  }

  @Test
  void nowIsReasonable() {
    var tp = new SystemTimeProvider();
    Instant before = Instant.now(Clock.systemUTC());
    Instant got = tp.now();
    Instant after = Instant.now(Clock.systemUTC());
    assertFalse(got.isBefore(before));
    assertFalse(got.isAfter(after.plus(Duration.ofSeconds(1))));
  }
}
