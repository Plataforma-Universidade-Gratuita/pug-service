package com.pug.academic.presenter.dtos;

import java.util.UUID;

/**
 * Course view DTO.
 *
 * @param id the course id
 * @param name the course name
 * @param school the associated school
 */
public record CourseView(UUID id, String name, SchoolResponse school) {}
