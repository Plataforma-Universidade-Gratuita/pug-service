package com.pug.academic.presenter.dtos;

import java.util.UUID;

/**
 * CourseResponse DTO.
 *
 * @param id the course id
 * @param name the course name
 * @param school the school response
 */
public record CourseResponse(UUID id, String name, SchoolResponse school) {}
