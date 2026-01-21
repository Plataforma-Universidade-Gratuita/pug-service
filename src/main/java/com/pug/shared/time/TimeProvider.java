package com.pug.shared.time;

import java.time.Clock;
import java.time.Instant;

/** Provides the current time and clock instance. */
public interface TimeProvider {
  /**
   * Gets the current instant.
   *
   * @return the current instant.
   */
  Instant now();

  /**
   * Gets the clock instance.
   *
   * @return the clock instance.
   */
  Clock clock();
}
