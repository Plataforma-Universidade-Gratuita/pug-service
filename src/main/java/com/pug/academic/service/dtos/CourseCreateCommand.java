package com.pug.academic.service.dtos;

import java.util.UUID;

/**
 * Command DTO for creating a new Course.
 *
 * @param name the name of the course.
 * @param schoolId the ID of the school this course belongs to.
 */
public record CourseCreateCommand(String name, UUID schoolId) {}
