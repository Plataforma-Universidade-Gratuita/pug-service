package com.pug.project.service.utils;

import com.pug.academic.domain.Student;
import com.pug.project.domain.Enrollment;
import com.pug.project.domain.Project;

/**
 * Stateless utility class responsible for mapping raw DTO command data into pure {@link Enrollment}
 * Domain Aggregates.
 */
public class EnrollmentProcessor {

  /**
   * Processes raw creation inputs and constructs a new {@link Enrollment} domain aggregate.
   *
   * @param student the fully reconstituted {@link Student} aggregate
   * @param project the fully reconstituted {@link Project} aggregate
   * @return a fully instantiated {@link Enrollment} domain aggregate, potentially containing errors
   */
  public static Enrollment processCreateInput(Student student, Project project) {
    return Enrollment.factory(student, project);
  }
}
