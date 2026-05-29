package br.org.catolicasc.pug.identity.service.utils;

import br.org.catolicasc.pug.identity.domain.enums.IdentityErrorCodes;
import br.org.catolicasc.pug.shared.exceptions.BusinessRuleException;
import br.org.catolicasc.pug.shared.exceptions.DuplicateResourceException;
import br.org.catolicasc.pug.shared.exceptions.ResourceNotFoundException;
import jakarta.ws.rs.NotAuthorizedException;

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
   * Instantiates a standardized {@link BusinessRuleException} indicating that the authenticated
   * account still needs to wire a password before it can use protected operations.
   *
   * @return a fully configured {@link BusinessRuleException} instance
   */
  public static BusinessRuleException accountPasswordSetupRequired() {
    return new BusinessRuleException(IdentityErrorCodes.ACCOUNT_PASSWORD_SETUP_REQUIRED);
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
   * Instantiates a standardized {@link BusinessRuleException} indicating that a proposed password
   * failed the platform's strength policy.
   *
   * @return a fully configured {@link BusinessRuleException} instance
   */
  public static BusinessRuleException weakPassword() {
    return new BusinessRuleException(IdentityErrorCodes.WEAK_PASSWORD);
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
}
