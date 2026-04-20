package br.org.catolicasc.pug.geo.service.utils;

import br.org.catolicasc.pug.geo.domain.enums.GeoErrorCodes;
import br.org.catolicasc.pug.shared.exceptions.ResourceNotFoundException;

/**
 * Utility class for centralizing the creation of common geographic domain exceptions.
 *
 * <p>This helper reduces boilerplate code across services by providing pre-configured exception
 * instances ready to be thrown, ensuring consistent error codes are used throughout the geographic
 * module.
 */
public final class ExceptionHelper {

  /** Private constructor to prevent instantiation. */
  private ExceptionHelper() {}

  /**
   * Instantiates a standardized {@link ResourceNotFoundException} indicating that a requested City
   * could not be located.
   *
   * @return a fully configured {@link ResourceNotFoundException} instance
   */
  public static ResourceNotFoundException cityNotFound() {
    return new ResourceNotFoundException(GeoErrorCodes.CITY_NOT_FOUND);
  }
}
