package com.pug.shared.domain.id;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class UuidV7Test {

  private static long epochMillisFrom(UUID uuid) {
    long msb = uuid.getMostSignificantBits();
    return (msb >>> 16) & 0xFFFFFFFFFFFFL;
  }

  @Test
  void versionAndVariantAreCorrect() {
    var u = UuidV7.next();
    assertEquals(7, u.version());
    assertEquals(2, u.variant());
  }

  @Test
  void timestampCloseToNow() {
    var u = UuidV7.next();
    long ts = epochMillisFrom(u);
    long now = Instant.now().toEpochMilli();
    assertTrue(Math.abs(now - ts) < 10_000);
  }

  @Test
  void manyAreUnique() {
    Set<UUID> set = new HashSet<>();
    for (int i = 0; i < 1000; i++) set.add(UuidV7.next());
    assertEquals(1000, set.size());
  }
}
