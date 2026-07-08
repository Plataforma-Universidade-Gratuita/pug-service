package br.org.catolicasc.pug.project.service.utils;

import br.org.catolicasc.pug.project.domain.enums.ProjectsErrorCodes;
import br.org.catolicasc.pug.shared.exceptions.BusinessRuleException;
import br.org.catolicasc.pug.shared.exceptions.DuplicateResourceException;
import br.org.catolicasc.pug.shared.exceptions.ResourceNotFoundException;

/**
 * Utility class for centralizing the creation of common Project domain exceptions.
 *
 * <p>This helper reduces boilerplate code across services by providing pre-configured exception
 * instances ready to be thrown, ensuring consistent error codes are used throughout the project
 * module.
 */
public final class ExceptionHelper {

  private ExceptionHelper() {}

  /**
   * Instantiates a standardized {@link ResourceNotFoundException} indicating that a requested
   * attendance could not be located.
   *
   * @return a fully configured {@link ResourceNotFoundException} instance
   */
  public static ResourceNotFoundException attendanceNotFound() {
    return new ResourceNotFoundException(ProjectsErrorCodes.ATTENDANCE_NOT_FOUND);
  }

  /**
   * Instantiates a standardized {@link BusinessRuleException} indicating that an attendance cannot
   * be created because the referenced enrollment does not exist.
   *
   * @return a fully configured {@link BusinessRuleException} instance
   */
  public static BusinessRuleException attendanceEnrollmentNotFound() {
    return new BusinessRuleException(ProjectsErrorCodes.ATTENDANCE_ENROLLMENT_NOT_FOUND);
  }

  /**
   * Instantiates a standardized {@link BusinessRuleException} indicating that an attendance cannot
   * be created because the referenced enrollment is not approved.
   *
   * @return a fully configured {@link BusinessRuleException} instance
   */
  public static BusinessRuleException attendanceEnrollmentNotApproved() {
    return new BusinessRuleException(ProjectsErrorCodes.ATTENDANCE_ENROLLMENT_NOT_APPROVED);
  }

  /**
   * Instantiates a standardized {@link BusinessRuleException} indicating that an attendance cannot
   * be validated as present because the project is not in progress.
   *
   * @return a fully configured {@link BusinessRuleException} instance
   */
  public static BusinessRuleException attendanceProjectNotInProgress() {
    return new BusinessRuleException(ProjectsErrorCodes.ATTENDANCE_PROJECT_NOT_IN_PROGRESS);
  }

  /**
   * Instantiates a standardized {@link DuplicateResourceException} indicating that an enrollment
   * record already exists.
   *
   * @return a fully configured {@link DuplicateResourceException} instance
   */
  public static DuplicateResourceException enrollmentAlreadyExists() {
    return new DuplicateResourceException(ProjectsErrorCodes.ENROLLMENT_ALREADY_EXISTS);
  }

  /**
   * Instantiates a standardized {@link ResourceNotFoundException} indicating that a requested
   * enrollment record could not be found.
   *
   * @return a fully configured {@link ResourceNotFoundException} instance
   */
  public static ResourceNotFoundException enrollmentNotFound() {
    return new ResourceNotFoundException(ProjectsErrorCodes.ENROLLMENT_NOT_FOUND);
  }

  /**
   * Instantiates a standardized {@link DuplicateResourceException} indicating that a project
   * already exists.
   *
   * @return a fully configured {@link DuplicateResourceException} instance
   */
  public static DuplicateResourceException projectAlreadyExists() {
    return new DuplicateResourceException(ProjectsErrorCodes.PROJECT_ALREADY_EXISTS);
  }

  /**
   * Instantiates a standardized {@link BusinessRuleException} indicating that a project cannot be
   * deleted because it has enrollments.
   *
   * @return a fully configured {@link BusinessRuleException} instance
   */
  public static BusinessRuleException projectHasEnrollments() {
    return new BusinessRuleException(ProjectsErrorCodes.PROJECT_HAS_ENROLLMENTS);
  }

  /**
   * Instantiates a standardized {@link ResourceNotFoundException} indicating that a requested
   * project could not be found.
   *
   * @return a fully configured {@link ResourceNotFoundException} instance
   */
  public static ResourceNotFoundException projectNotFound() {
    return new ResourceNotFoundException(ProjectsErrorCodes.PROJECT_NOT_FOUND);
  }
}
