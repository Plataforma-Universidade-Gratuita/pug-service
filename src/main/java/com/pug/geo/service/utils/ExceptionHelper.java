package com.pug.geo.service.utils;

import com.pug.geo.domain.enums.GeoErrorCodes;
import com.pug.shared.exceptions.BusinessRuleException;
import com.pug.shared.exceptions.DuplicateResourceException;
import com.pug.shared.exceptions.ResourceNotFoundException;

/**
 * Utility class for centralizing the creation of common geographic domain exceptions.
 * <p>
 * This helper reduces boilerplate code across services by providing pre-configured
 * exception instances ready to be thrown, ensuring consistent error codes are used
 * throughout the geographic module.
 */
public final class ExceptionHelper {

    /**
     * Private constructor to prevent instantiation.
     */
    private ExceptionHelper() {
    }

    /**
     * Instantiates a standardized {@link ResourceNotFoundException} indicating
     * that a requested City could not be located.
     *
     * @return a fully configured {@link ResourceNotFoundException} instance
     */
    public static ResourceNotFoundException cityNotFound() {
        return new ResourceNotFoundException(GeoErrorCodes.CITY_NOT_FOUND);
    }

    /**
     * Instantiates a standardized {@link DuplicateResourceException} indicating
     * that a City with the specified IBGE code already exists in the system.
     *
     * @return a fully configured {@link DuplicateResourceException} instance
     */
    public static DuplicateResourceException cityAlreadyExists() {
        return new DuplicateResourceException(GeoErrorCodes.CITY_ALREADY_EXISTS);
    }

    /**
     * Instantiates a standardized {@link BusinessRuleException} indicating
     * that an operation cannot be performed on a default City (e.g., Jaraguá do Sul or Joinville).
     *
     * @return a fully configured {@link BusinessRuleException} instance
     */
    public static BusinessRuleException cityIsDefault() {
        return new BusinessRuleException(GeoErrorCodes.CITY_IS_DEFAULT);
    }

    /**
     * Instantiates a standardized {@link BusinessRuleException} indicating
     * that a City cannot be deleted because it is still referenced by other entities (e.g., Partners).
     *
     * @return a fully configured {@link BusinessRuleException} instance
     */
    public static BusinessRuleException cityStillReferencedByEntity() {
        return new BusinessRuleException(GeoErrorCodes.CITY_STILL_REFERENCED_BY_ENTITY);
    }
}