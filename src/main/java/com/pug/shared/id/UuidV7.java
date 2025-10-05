package com.pug.shared.id;

import com.github.f4b6a3.uuid.UuidCreator;
import jakarta.inject.Singleton;
import java.util.UUID;

/**
 * Injectable UUID generator for services, tests, and HTTP filters. Use for correlation IDs,
 * idempotency keys, and pre-persist IDs. Hibernate entity PKs should keep their own generator.
 */
@Singleton
public class UuidV7 {
  public UUID next() {
    return UuidCreator.getTimeOrderedEpoch();
  }

  public String nextString() {
    return next().toString();
  }
}
