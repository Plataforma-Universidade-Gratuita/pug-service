package com.pug.project.infra.read.dtos;

import com.pug.academic.infra.read.dtos.SchoolView;
import java.util.List;

/**
 * Data Transfer Object (DTO) representing a read-only, consolidated view of a School and its
 * associated projects.
 *
 * <p>Following CQRS principles, this record is used exclusively for returning queried data to the
 * client, grouping a specific {@link SchoolView} with all {@link ProjectView} records linked to it.
 *
 * @param school the read-only projection of the school
 * @param projects the list of projects associated with this school
 */
public record SchoolProjectView(SchoolView school, List<ProjectView> projects) {}
