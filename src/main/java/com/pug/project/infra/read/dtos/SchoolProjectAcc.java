package com.pug.project.infra.read.dtos;

import com.pug.academic.infra.persistence.SchoolEntity;
import com.pug.project.infra.persistence.ProjectEntity;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Internal Data Transfer Object (DTO) used exclusively for JPA tuple projections.
 *
 * <p>By fetching the {@link ProjectEntity} and {@link SchoolEntity} in a single query projection,
 * it allows the query layer to group projects by school without triggering multiple database
 * round-trips.
 */
@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public record SchoolProjectAcc(ProjectEntity project, SchoolEntity school) {}
