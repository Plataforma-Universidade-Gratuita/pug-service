package com.pug.academic.service.dtos;

import java.util.List;

/**
 * Command for bulk creation of courses.
 *
 * @param names list of course names
 * @param schoolName the name of the school to which the courses belong
 */
public record CourseCreateBulkCommand(List<String> names, String schoolName) {}
