package com.pug.project.service.utils;

import com.pug.project.domain.enums.ProjectsErrorCodes;
import com.pug.shared.exceptions.BusinessRuleException;
import com.pug.shared.exceptions.DuplicateResourceException;
import com.pug.shared.exceptions.ResourceNotFoundException;

/**
 * Utility class for centralizing the creation of common Project domain exceptions.
 *
 * <p>This helper reduces boilerplate code across services by providing pre-configured exception
 * instances ready to be thrown, ensuring consistent error codes are used throughout the project
 * module.
 */
public final class ExceptionHelper {

  /** Private constructor to prevent instantiation. */
  private ExceptionHelper() {}

  /**
   * Instantiates a standardized {@link DuplicateResourceException} indicating that an attendance
   * record already exists.
   *
   * @return a fully configured {@link DuplicateResourceException} instance
   */
  public static DuplicateResourceException attendanceAlreadyExists() {
    return new DuplicateResourceException(ProjectsErrorCodes.ATTENDANCE_ALREADY_EXISTS);
  }

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
