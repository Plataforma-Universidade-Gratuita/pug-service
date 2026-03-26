package com.pug.project.presenter.dtos;

import com.pug.academic.presenter.dtos.SchoolResponse;
import java.util.List;

/**
 * Data Transfer Object (DTO) used as the standardized API JSON response for a School and its
 * associated Projects.
 *
 * @param school the consolidated, client-facing projection of the school
 * @param projects the list of consolidated, client-facing projections of associated projects
 */
public record ProjectsBySchoolResponse(SchoolResponse school, List<ProjectResponse> projects) {}
