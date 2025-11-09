package com.pug.projects.domain.enums;

import lombok.Getter;

@Getter
public enum ProjectStatus {
  PLANNED("project.status.planned"),
  IN_PROGRESS("project.status.in_progress"),
  COMPLETED("project.status.completed"),
  ON_HOLD("project.status.on_hold"),
  CANCELLED("project.status.cancelled");

  private final String bundleKey;

  ProjectStatus(String bundleKey) {
    this.bundleKey = bundleKey;
  }
}
