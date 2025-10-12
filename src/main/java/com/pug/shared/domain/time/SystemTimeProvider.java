package com.pug.shared.domain.time;

import jakarta.enterprise.context.ApplicationScoped;
import java.time.Clock;
import java.time.Instant;

@ApplicationScoped
public class SystemTimeProvider implements TimeProvider {
  private final Clock clock = Clock.systemUTC();

  @Override
  public Instant now() {
    return Instant.now(clock);
  }

  @Override
  public Clock clock() {
    return clock;
  }
}
