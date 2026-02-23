package com.pug.projects.domain.enums;

import lombok.Getter;

/** Enumeration representing the status of a project. */
@Getter
public enum ProjectStatus {
  PLANNED("project.status.planned"),
  IN_PROGRESS("project.status.in.progress"),
  COMPLETED("project.status.completed"),
  ON_HOLD("project.status.on.hold"),
  CANCELED("project.status.canceled");

  private final String bundleKey;

  ProjectStatus(String bundleKey) {
    this.bundleKey = bundleKey;
  }
}
