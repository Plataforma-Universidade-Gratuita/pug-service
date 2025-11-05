package com.pug.helpers.entityGenerators;

import com.pug.projects.infra.persistence.ProjectsBySchoolsEntity;
import java.util.UUID;

public class ProjectsBySchoolsEntityGenerator {

  /**
   * Helper method to create a random ProjectsBySchoolsEntity object.
   *
   * @param projectId The UUID of the project.
   * @param schoolId The UUID of the school.
   */
  public ProjectsBySchoolsEntity createRandomProjectsBySchoolsEntity(
      UUID projectId, UUID schoolId) {
    return new ProjectsBySchoolsEntity(
        new ProjectsBySchoolsEntity.ProjectsBySchoolsId(projectId, schoolId));
  }
}
