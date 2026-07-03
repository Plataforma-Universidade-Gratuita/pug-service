/*
 * Copyright (c) 2026 Mateus Fernandes and Plataforma Universidade Gratuita.
 * All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized use,
 * copying, modification, distribution, or deployment is prohibited.
 */

package br.org.catolicasc.pug.partner.service.utils;

import br.org.catolicasc.pug.partner.domain.enums.PartnerErrorCodes;
import br.org.catolicasc.pug.shared.exceptions.BusinessRuleException;
import br.org.catolicasc.pug.shared.exceptions.DuplicateResourceException;
import br.org.catolicasc.pug.shared.exceptions.ResourceNotFoundException;
import jakarta.ws.rs.NotAuthorizedException;

/**
 * Utility class for centralizing the creation of common Partner domain exceptions.
 *
 * <p>This helper reduces boilerplate code across partner services by providing pre-configured
 * exception instances ready to be thrown, ensuring consistent error codes are used throughout the
 * partner module.
 */
public final class ExceptionHelper {

  /** Private constructor to prevent instantiation. */
  private ExceptionHelper() {}

  /**
   * Instantiates a standardized {@link DuplicateResourceException} indicating that a partner entity
   * already exists in the system.
   *
   * @return a fully configured {@link DuplicateResourceException} instance
   */
  public static DuplicateResourceException entityAlreadyExists() {
    return new DuplicateResourceException(PartnerErrorCodes.ENTITY_ALREADY_EXISTS);
  }

  /**
   * Instantiates a standardized {@link BusinessRuleException} indicating that a partner entity
   * cannot be removed because it is linked to existing projects.
   *
   * @return a fully configured {@link BusinessRuleException} instance
   */
  public static BusinessRuleException entityHasProjects() {
    return new BusinessRuleException(PartnerErrorCodes.ENTITY_HAS_PROJECTS);
  }

  /**
   * Instantiates a standardized {@link ResourceNotFoundException} indicating that a requested
   * partner entity could not be located.
   *
   * @return a fully configured {@link ResourceNotFoundException} instance
   */
  public static ResourceNotFoundException entityNotFound() {
    return new ResourceNotFoundException(PartnerErrorCodes.ENTITY_NOT_FOUND);
  }

  /**
   * Instantiates a standardized {@link DuplicateResourceException} indicating that a staff member
   * already exists in the system.
   *
   * @return a fully configured {@link DuplicateResourceException} instance
   */
  public static DuplicateResourceException staffAlreadyExists() {
    return new DuplicateResourceException(PartnerErrorCodes.STAFF_ALREADY_EXISTS);
  }

  /**
   * Instantiates a standardized {@link BusinessRuleException} indicating that a staff member is
   * already assigned to another partner entity.
   *
   * @return a fully configured {@link BusinessRuleException} instance
   */
  public static BusinessRuleException staffAssignedToOtherEntity() {
    return new BusinessRuleException(PartnerErrorCodes.STAFF_ASSIGNED_TO_OTHER_ENTITY);
  }

  /**
   * Instantiates a standardized {@link BusinessRuleException} indicating that the staff member's
   * email address is already used within the same partner entity.
   *
   * @return a fully configured {@link BusinessRuleException} instance
   */
  public static BusinessRuleException staffEmailAlreadyExistsInEntity() {
    return new BusinessRuleException(PartnerErrorCodes.STAFF_EMAIL_ALREADY_EXISTS_IN_ENTITY);
  }

  /**
   * Instantiates a standardized {@link BusinessRuleException} indicating that a staff member cannot
   * be removed because it is linked to existing attendances.
   *
   * @return a fully configured {@link BusinessRuleException} instance
   */
  public static BusinessRuleException staffHasAttendances() {
    return new BusinessRuleException(PartnerErrorCodes.STAFF_HAS_ATTENDANCES);
  }

  /**
   * Instantiates a standardized {@link BusinessRuleException} indicating that a staff member cannot
   * be removed because it is linked to existing projects.
   *
   * @return a fully configured {@link BusinessRuleException} instance
   */
  public static BusinessRuleException staffHasProjects() {
    return new BusinessRuleException(PartnerErrorCodes.STAFF_HAS_PROJECTS);
  }

  /**
   * Instantiates a standardized {@link ResourceNotFoundException} indicating that a requested staff
   * member could not be located.
   *
   * @return a fully configured {@link ResourceNotFoundException} instance
   */
  public static ResourceNotFoundException staffNotFound() {
    return new ResourceNotFoundException(PartnerErrorCodes.STAFF_NOT_FOUND);
  }

  /**
   * Instantiates a standardized {@link NotAuthorizedException} indicating that an authentication
   * attempt failed due to invalid credentials or an inactive account state.
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
