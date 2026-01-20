package com.pug.shared.time;

import jakarta.enterprise.context.ApplicationScoped;

import java.time.Clock;
import java.time.Instant;

/**
 * A TimeProvider implementation that provides the current system time using UTC clock.
 */
@ApplicationScoped
public class SystemTimeProvider implements TimeProvider {
  private final Clock clock = Clock.systemUTC();

  /**
   * Gets the current system time as an Instant.
   *
   * @return the current Instant.
   */
  @Override
  public Instant now() {
    return Instant.now(clock);
  }

  /**
   * Gets the Clock instance used by this TimeProvider.
   *
   * @return the Clock instance.
   */
  @Override
  public Clock clock() {
    return clock;
  }
}