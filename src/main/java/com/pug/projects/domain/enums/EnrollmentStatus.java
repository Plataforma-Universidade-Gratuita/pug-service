package com.pug.projects.domain.enums;

import lombok.Getter;

@Getter
public enum EnrollmentStatus {
  PENDING("enrollment.status.pending"),
  APPROVED("enrollment.status.approved"),
  REJECTED("enrollment.status.rejected"),
  EXITED("enrollment.status.exited"),
  REMOVED("enrollment.status.removed"),
  CANCELED("enrollment.status.canceled"),
  COMPLETED("enrollment.status.completed");

  private final String bundleKey;

  EnrollmentStatus(String bundleKey) {
    this.bundleKey = bundleKey;
  }
}
