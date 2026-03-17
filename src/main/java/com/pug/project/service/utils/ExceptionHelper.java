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

  public static DuplicateResourceException attendanceAlreadyExists() {
    return new DuplicateResourceException(ProjectsErrorCodes.ATTENDANCE_ALREADY_EXISTS);
  }

  public static ResourceNotFoundException attendanceNotFound() {
    return new ResourceNotFoundException(ProjectsErrorCodes.ATTENDANCE_NOT_FOUND);
  }

  public static DuplicateResourceException enrollmentAlreadyExists() {
    return new DuplicateResourceException(ProjectsErrorCodes.ENROLLMENT_ALREADY_EXISTS);
  }

  public static ResourceNotFoundException enrollmentNotFound() {
    return new ResourceNotFoundException(ProjectsErrorCodes.ENROLLMENT_NOT_FOUND);
  }

  public static DuplicateResourceException projectAlreadyExists() {
    return new DuplicateResourceException(ProjectsErrorCodes.PROJECT_ALREADY_EXISTS);
  }

  public static BusinessRuleException projectHasEnrollments() {
    return new BusinessRuleException(ProjectsErrorCodes.PROJECT_HAS_ENROLLMENTS);
  }

  public static ResourceNotFoundException projectNotFound() {
    return new ResourceNotFoundException(ProjectsErrorCodes.PROJECT_NOT_FOUND);
  }
}
