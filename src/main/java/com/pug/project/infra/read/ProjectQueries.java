package com.pug.project.infra.read;

import com.pug.project.infra.read.dtos.ProjectView;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** Read-only interface for executing queries against Projects. */
public interface ProjectQueries {
  Optional<ProjectView> findOptionalById(UUID id);

  List<ProjectView> listAllProjects();

  List<ProjectView> listByEntityId(UUID entityId);

  List<ProjectView> searchByName(String query);
}
