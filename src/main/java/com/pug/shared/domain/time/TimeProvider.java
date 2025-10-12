package com.pug.shared.domain.time;

import java.time.Clock;
import java.time.Instant;

public interface TimeProvider {
  Instant now();

  Clock clock();
}
