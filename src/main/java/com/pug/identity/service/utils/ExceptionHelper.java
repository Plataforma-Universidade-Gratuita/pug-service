package com.pug.identity.service.utils;

import com.pug.identity.domain.enums.IdentityErrorCodes;
import com.pug.shared.exceptions.DuplicateResourceException;
import com.pug.shared.exceptions.ResourceNotFoundException;

/**
 * Utility class for centralizing the creation of common Identity domain exceptions.
 *
 * <p>This helper reduces boilerplate code across services by providing pre-configured exception
 * instances ready to be thrown, ensuring consistent error codes are used throughout the identity
 * module.
 */
public final class ExceptionHelper {

  /** Private constructor to prevent instantiation. */
  private ExceptionHelper() {}

  /**
   * Instantiates a standardized {@link DuplicateResourceException} indicating that a User with the
   * specified CPF already exists in the system.
   *
   * @return a fully configured {@link DuplicateResourceException} instance
   */
  public static DuplicateResourceException userAlreadyExists() {
    return new DuplicateResourceException(IdentityErrorCodes.USER_ALREADY_EXISTS);
  }

  /**
   * Instantiates a standardized {@link ResourceNotFoundException} indicating that a requested User
   * could not be located.
   *
   * @return a fully configured {@link ResourceNotFoundException} instance
   */
  public static ResourceNotFoundException userNotFound() {
    return new ResourceNotFoundException(IdentityErrorCodes.USER_NOT_FOUND);
  }

  /**
   * Instantiates a standardized {@link ResourceNotFoundException} indicating that a requested
   * Administrator profile could not be located.
   *
   * @return a fully configured {@link ResourceNotFoundException} instance
   */
  public static ResourceNotFoundException adminNotFound() {
    return new ResourceNotFoundException(IdentityErrorCodes.ADMIN_NOT_FOUND);
  }

  /**
   * Instantiates a standardized {@link DuplicateResourceException} indicating that an Account with
   * the specified email address already exists in the system.
   *
   * @return a fully configured {@link DuplicateResourceException} instance
   */
  public static DuplicateResourceException accountAlreadyExists() {
    return new DuplicateResourceException(IdentityErrorCodes.ACCOUNT_ALREADY_EXISTS);
  }

  /**
   * Instantiates a standardized {@link ResourceNotFoundException} indicating that a requested
   * authentication Account could not be located.
   *
   * @return a fully configured {@link ResourceNotFoundException} instance
   */
  public static ResourceNotFoundException accountNotFound() {
    return new ResourceNotFoundException(IdentityErrorCodes.ACCOUNT_NOT_FOUND);
  }
}
