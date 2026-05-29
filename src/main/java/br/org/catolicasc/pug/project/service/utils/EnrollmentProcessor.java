package br.org.catolicasc.pug.project.service.utils;

import br.org.catolicasc.pug.academic.domain.FormerStudent;
import br.org.catolicasc.pug.project.domain.Enrollment;
import br.org.catolicasc.pug.project.domain.Project;
import br.org.catolicasc.pug.shared.exceptions.AppValidationException;

/**
 * Stateless utility class responsible for mapping raw DTO command data into pure {@link Enrollment}
 * Domain Aggregates.
 *
 * <p>This processor centralizes the orchestration of domain factory methods, ensuring that the
 * application service layer remains focused on coordination and error handling rather than on the
 * low-level details of aggregate construction.
 */
public final class EnrollmentProcessor {

  /**
   * Processes raw creation inputs and constructs a new {@link Enrollment} domain aggregate.
   *
   * <p>This method expects fully reconstituted {@link FormerStudent} and {@link Project} aggregates, and
   * delegates the initialization of the enrollment to {@link Enrollment#factory(FormerStudent, Project)}.
   * The returned instance is immediately self-validated; any accumulated domain validation problems
   * can be inspected via {@link Enrollment#hasFieldErrors()} and {@link
   * Enrollment#getFieldErrors()} and should be translated into an {@link AppValidationException} by
   * the calling service when appropriate.
   *
   * @param formerStudent the fully reconstituted {@link FormerStudent} aggregate to enroll
   * @param project the fully reconstituted {@link Project} aggregate representing the target
   *     project
   * @return a fully instantiated {@link Enrollment} domain aggregate, potentially containing
   *     validation errors
   */
  public static Enrollment processCreateInput(FormerStudent formerStudent, Project project) {
    return Enrollment.factory(formerStudent, project);
  }
}

