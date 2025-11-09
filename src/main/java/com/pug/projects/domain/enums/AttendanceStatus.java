package com.pug.projects.domain.enums;

import lombok.Getter;

@Getter
public enum AttendanceStatus {
  WAITING("attendance.status.waiting"),
  PRESENT("attendance.status.present"),
  ABSENT("attendance.status.absent");

  private final String bundleKey;

  AttendanceStatus(String bundleKey) {
    this.bundleKey = bundleKey;
  }
}
