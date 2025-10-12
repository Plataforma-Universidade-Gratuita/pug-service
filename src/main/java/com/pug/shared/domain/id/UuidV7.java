package com.pug.shared.domain.id;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

/** UUIDv7 generator with a 12-bit monotonic counter. */
public final class UuidV7 {
  private static final Random RNG = new SecureRandom();
  private static final AtomicInteger COUNTER = new AtomicInteger(0);

  private UuidV7() {}

  public static UUID next() {
    long millis = Instant.now().toEpochMilli();
    int count = COUNTER.updateAndGet(x -> (x + 1) & 0x0FFF);

    long msb = (millis & 0xFFFFFFFFFFFFL) << 16;
    msb |= 0x7L << 12;
    msb |= (count & 0x0FFFL);

    long rand = RNG.nextLong();
    long lsb = (rand & 0x3FFFFFFFFFFFFFFFL) | 0x8000000000000000L;

    return new UUID(msb, lsb);
  }
}
