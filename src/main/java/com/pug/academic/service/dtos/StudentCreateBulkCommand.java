package com.pug.academic.service.dtos;

import java.util.List;

/**
 * Command to create multiple students in bulk.
 *
 * @param studentsCommand the list of commands containing the data to create each student.
 * @param courseName      the name of the course to which the students will be enrolled.
 */
public record StudentCreateBulkCommand(
        List<StudentCreateCommand> studentsCommand,
        String courseName
) {
}
