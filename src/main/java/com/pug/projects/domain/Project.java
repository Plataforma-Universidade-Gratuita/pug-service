package com.pug.projects.domain;

import com.pug.academic.domain.School;
import com.pug.partner.domain.Entity;
import com.pug.projects.domain.enums.ProjectStatus;
import com.pug.projects.domain.vos.ProjectHours;
import com.pug.projects.domain.vos.ProjectInfo;
import java.util.List;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/** Domain entity representing a Project. */
@Getter
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Project {
  private final UUID id;
  private final String name;
  private final Entity entity;
  private final String description;
  private final ProjectHours projectHours;
  private final ProjectInfo projectInfo;
  private final ProjectStatus projectStatus;
  private final List<School> schools;

  private void validate() {}

  /** Builder class for constructing Project instances with validation. */
  public static class ProjectBuilder {
    /**
     * Builds the Project instance and performs validation.
     *
     * @return the validated Project instance.
     */
    public Project build() {
      Project c =
          new Project(
              id, name, entity, description, projectHours, projectInfo, projectStatus, schools);
      c.validate();
      return c;
    }
  }
}
