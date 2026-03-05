package com.pug.partner.service.utils;

import com.pug.partner.domain.enums.PartnerErrorCodes;
import com.pug.shared.exceptions.BusinessRuleException;
import com.pug.shared.exceptions.DuplicateResourceException;
import com.pug.shared.exceptions.ResourceNotFoundException;

/**
 * Utility class for centralizing the creation of common Partner domain exceptions.
 *
 * <p>This helper reduces boilerplate code across services by providing pre-configured exception
 * instances ready to be thrown, ensuring consistent error codes are used throughout the partner
 * module.
 */
public final class ExceptionHelper {

  /** Private constructor to prevent instantiation. */
  private ExceptionHelper() {}

  /**
   * Instantiates a standardized {@link ResourceNotFoundException} indicating that a requested
   * Partner Entity could not be located.
   *
   * @return a fully configured {@link ResourceNotFoundException} instance
   */
  public static ResourceNotFoundException entityNotFound() {
    return new ResourceNotFoundException(PartnerErrorCodes.ENTITY_NOT_FOUND);
  }

  /**
   * Instantiates a standardized {@link DuplicateResourceException} indicating that a Partner Entity
   * with the specified CNPJ already exists in the system.
   *
   * @return a fully configured {@link DuplicateResourceException} instance
   */
  public static DuplicateResourceException entityAlreadyExists() {
    return new DuplicateResourceException(PartnerErrorCodes.ENTITY_ALREADY_EXISTS);
  }

  /**
   * Instantiates a standardized {@link ResourceNotFoundException} indicating that a requested Staff
   * assignment could not be located.
   *
   * @return a fully configured {@link ResourceNotFoundException} instance
   */
  public static ResourceNotFoundException staffNotFound() {
    return new ResourceNotFoundException(PartnerErrorCodes.STAFF_NOT_FOUND);
  }

  /**
   * Instantiates a standardized {@link DuplicateResourceException} indicating that a Staff member
   * assignment already exists for the given account and entity.
   *
   * @return a fully configured {@link DuplicateResourceException} instance
   */
  public static DuplicateResourceException staffAlreadyExists() {
    return new DuplicateResourceException(PartnerErrorCodes.STAFF_ALREADY_EXISTS);
  }

  /**
   * Instantiates a standardized {@link BusinessRuleException} indicating that an account is already
   * assigned as Staff to a different partner entity.
   *
   * @return a fully configured {@link BusinessRuleException} instance
   */
  public static BusinessRuleException staffAssignedToOtherEntity() {
    return new BusinessRuleException(PartnerErrorCodes.STAFF_ASSIGNED_TO_OTHER_ENTITY);
  }
}
