package br.org.catolicasc.pug.academic.service.utils;

import br.org.catolicasc.pug.academic.domain.enums.AcademicErrorCodes;
import br.org.catolicasc.pug.shared.exceptions.BusinessRuleException;
import br.org.catolicasc.pug.shared.exceptions.DuplicateResourceException;
import br.org.catolicasc.pug.shared.exceptions.ResourceNotFoundException;
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
   * cannot be modified or deleted because it currently has active formerStudent enrollments.
   *
   * @return a fully configured {@link BusinessRuleException} instance
   */
  public static BusinessRuleException courseHasStudents() {
    return new BusinessRuleException(AcademicErrorCodes.COURSE_HAS_FORMER_STUDENTS);
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
   * Instantiates a standardized {@link DuplicateResourceException} indicating that a
   * AreaOfExpertise with the specified name already exists in the system.
   *
   * @return a fully configured {@link DuplicateResourceException} instance
   */
  public static DuplicateResourceException areOfExpertiseAlreadyExists() {
    return new DuplicateResourceException(AcademicErrorCodes.AREA_OF_EXPERTISE_ALREADY_EXISTS);
  }

  /**
   * Instantiates a standardized {@link BusinessRuleException} indicating that an academic areaOfExpertise
   * cannot be modified or deleted because it currently has active courses associated with it.
   *
   * @return a fully configured {@link BusinessRuleException} instance
   */
  public static BusinessRuleException areaOfExpertiseHasCourses() {
    return new BusinessRuleException(AcademicErrorCodes.AREA_OF_EXPERTISE_HAS_COURSES);
  }

  /**
   * Instantiates a standardized {@link ResourceNotFoundException} indicating that a requested
   * Academic AreaOfExpertise could not be located.
   *
   * @return a fully configured {@link ResourceNotFoundException} instance
   */
  public static ResourceNotFoundException areaOfExpertiseNotFound() {
    return new ResourceNotFoundException(AcademicErrorCodes.AREA_OF_EXPERTISE_NOT_FOUND);
  }

  /**
   * Instantiates a standardized {@link DuplicateResourceException} indicating that a FormerStudent
   * with the specified academic registration already exists in the system.
   *
   * @return a fully configured {@link DuplicateResourceException} instance
   */
  public static DuplicateResourceException formerStudentAlreadyExists() {
    return new DuplicateResourceException(AcademicErrorCodes.FORMER_STUDENT_ALREADY_EXISTS);
  }

  /**
   * Instantiates a standardized {@link BusinessRuleException} indicating that a formerStudent
   * profile cannot be modified or deleted because it still retains active or historical project
   * enrollments.
   *
   * @return a fully configured {@link BusinessRuleException} instance
   */
  public static BusinessRuleException formerStudentHasEnrollments() {
    return new BusinessRuleException(AcademicErrorCodes.FORMER_STUDENT_HAS_ENROLLMENTS);
  }

  /**
   * Instantiates a standardized {@link ResourceNotFoundException} indicating that a requested
   * FormerStudent enrollment record could not be located.
   *
   * @return a fully configured {@link ResourceNotFoundException} instance
   */
  public static ResourceNotFoundException formerStudentNotFound() {
    return new ResourceNotFoundException(AcademicErrorCodes.FORMER_STUDENT_NOT_FOUND);
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
