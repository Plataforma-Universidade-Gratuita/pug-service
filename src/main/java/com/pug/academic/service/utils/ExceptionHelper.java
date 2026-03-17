package com.pug.academic.service.utils;

import com.pug.academic.domain.enums.AcademicErrorCodes;
import com.pug.shared.exceptions.BusinessRuleException;
import com.pug.shared.exceptions.DuplicateResourceException;
import com.pug.shared.exceptions.ResourceNotFoundException;
import jakarta.ws.rs.NotAuthorizedException;

/**
 * Utility class for centralizing the creation of common Academic domain exceptions.
 *
 * <p>This helper reduces boilerplate code across services by providing pre-configured exception
 * instances ready to be thrown, ensuring consistent error codes are used throughout the academic
 * module.
 */
public final class ExceptionHelper {

  /** Private constructor to prevent instantiation. */
  private ExceptionHelper() {}

  /**
   * Instantiates a standardized {@link DuplicateResourceException} indicating that a Course with
   * the specified name already exists in the system.
   *
   * @return a fully configured {@link DuplicateResourceException} instance
   */
  public static DuplicateResourceException courseAlreadyExists() {
    return new DuplicateResourceException(AcademicErrorCodes.COURSE_ALREADY_EXISTS);
  }

  /**
   * Instantiates a standardized {@link BusinessRuleException} indicating that an academic course
   * cannot be modified or deleted because it currently has active student enrollments.
   *
   * @return a fully configured {@link BusinessRuleException} instance
   */
  public static BusinessRuleException courseHasStudents() {
    return new BusinessRuleException(AcademicErrorCodes.COURSE_HAS_STUDENTS);
  }

  /**
   * Instantiates a standardized {@link ResourceNotFoundException} indicating that a requested
   * Academic Course could not be located.
   *
   * @return a fully configured {@link ResourceNotFoundException} instance
   */
  public static ResourceNotFoundException courseNotFound() {
    return new ResourceNotFoundException(AcademicErrorCodes.COURSE_NOT_FOUND);
  }

  /**
   * Instantiates a standardized {@link DuplicateResourceException} indicating that a School with
   * the specified name already exists in the system.
   *
   * @return a fully configured {@link DuplicateResourceException} instance
   */
  public static DuplicateResourceException schoolAlreadyExists() {
    return new DuplicateResourceException(AcademicErrorCodes.SCHOOL_ALREADY_EXISTS);
  }

  /**
   * Instantiates a standardized {@link BusinessRuleException} indicating that an academic school
   * cannot be modified or deleted because it currently has active courses associated with it.
   *
   * @return a fully configured {@link BusinessRuleException} instance
   */
  public static BusinessRuleException schoolHasCourses() {
    return new BusinessRuleException(AcademicErrorCodes.SCHOOL_HAS_COURSES);
  }

  /**
   * Instantiates a standardized {@link ResourceNotFoundException} indicating that a requested
   * Academic School could not be located.
   *
   * @return a fully configured {@link ResourceNotFoundException} instance
   */
  public static ResourceNotFoundException schoolNotFound() {
    return new ResourceNotFoundException(AcademicErrorCodes.SCHOOL_NOT_FOUND);
  }

  /**
   * Instantiates a standardized {@link DuplicateResourceException} indicating that a Student with
   * the specified academic registration already exists in the system.
   *
   * @return a fully configured {@link DuplicateResourceException} instance
   */
  public static DuplicateResourceException studentAlreadyExists() {
    return new DuplicateResourceException(AcademicErrorCodes.STUDENT_ALREADY_EXISTS);
  }

  /**
   * Instantiates a standardized {@link BusinessRuleException} indicating that a student profile
   * cannot be modified or deleted because it still retains active or historical project
   * enrollments.
   *
   * @return a fully configured {@link BusinessRuleException} instance
   */
  public static BusinessRuleException studentHasEnrollments() {
    return new BusinessRuleException(AcademicErrorCodes.STUDENT_HAS_ENROLLMENTS);
  }

  /**
   * Instantiates a standardized {@link ResourceNotFoundException} indicating that a requested
   * Student enrollment record could not be located.
   *
   * @return a fully configured {@link ResourceNotFoundException} instance
   */
  public static ResourceNotFoundException studentNotFound() {
    return new ResourceNotFoundException(AcademicErrorCodes.STUDENT_NOT_FOUND);
  }

  /**
   * Instantiates a standardized {@link NotAuthorizedException} indicating that an authentication
   * attempt or secure request failed due to invalid credentials, an inactive account state, or a
   * missing token.
   *
   * <p>This exception is intercepted by the platform's global exception mapper to return a generic,
   * safe HTTP 401 response without leaking which part of the validation failed.
   *
   * @return a fully configured {@link NotAuthorizedException} instance
   */
  public static NotAuthorizedException unauthorized() {
    return new NotAuthorizedException("Invalid credentials or inactive account");
  }
}
