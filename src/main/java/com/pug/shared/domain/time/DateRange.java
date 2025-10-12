package com.pug.shared.domain.time;

import java.time.LocalDate;

public record DateRange(LocalDate start, LocalDate end) {
  public DateRange {
    if (start == null || end == null) throw new IllegalArgumentException("start/end required");
    if (end.isBefore(start)) throw new IllegalArgumentException("end >= start required");
  }

  public boolean contains(LocalDate d) {
    return (d.isAfter(start) || d.isEqual(start)) && (d.isBefore(end) || d.isEqual(end));
  }
}
