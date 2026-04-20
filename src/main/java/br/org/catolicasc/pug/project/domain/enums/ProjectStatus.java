package br.org.catolicasc.pug.project.domain.enums;

import br.org.catolicasc.pug.shared.domain.enums.GenericCodes;
import lombok.Getter;

/**
 * Enumeration representing the valid lifecycle states of a Project.
 *
 * <p>Implements {@link GenericCodes} to allow the presentation layer to easily translate these
 * statuses into localized, human-readable strings.
 */
@Getter
public enum ProjectStatus implements GenericCodes {

  /** Indicates the project was aborted before or during execution. */
  CANCELED("project.status.canceled"),

  /** Indicates the project has successfully finished. */
  COMPLETED("project.status.completed"),

  /** Indicates the project is currently active and students are participating. */
  IN_PROGRESS("project.status.in.progress"),

  /** Indicates the project is temporarily paused. */
  ON_HOLD("project.status.on.hold"),

  /** Indicates the project is created but has not yet started. */
  PLANNED("project.status.planned");

  private final String bundleKey;

  ProjectStatus(String bundleKey) {
    this.bundleKey = bundleKey;
  }
}
