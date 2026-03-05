package com.pug.academic.service.utils;

import com.pug.academic.domain.enums.AcademicErrorCodes;
import com.pug.shared.exceptions.DuplicateResourceException;
import com.pug.shared.exceptions.ResourceNotFoundException;

/**
 * Utility class for centralizing the creation of common Academic domain exceptions.
 * <p>
 * This helper reduces boilerplate code across services by providing pre-configured
 * exception instances ready to be thrown, ensuring consistent error codes are used
 * throughout the academic module.
 */
public final class ExceptionHelper {

    /**
     * Private constructor to prevent instantiation.
     */
    private ExceptionHelper() {
    }

    /**
     * Instantiates a standardized {@link ResourceNotFoundException} indicating
     * that a requested Academic School could not be located.
     *
     * @return a fully configured {@link ResourceNotFoundException} instance
     */
    public static ResourceNotFoundException schoolNotFound() {
        return new ResourceNotFoundException(AcademicErrorCodes.SCHOOL_NOT_FOUND);
    }

    /**
     * Instantiates a standardized {@link DuplicateResourceException} indicating
     * that a School with the specified name already exists in the system.
     *
     * @return a fully configured {@link DuplicateResourceException} instance
     */
    public static DuplicateResourceException schoolAlreadyExists() {
        return new DuplicateResourceException(AcademicErrorCodes.SCHOOL_ALREADY_EXISTS);
    }

    /**
     * Instantiates a standardized {@link ResourceNotFoundException} indicating
     * that a requested Academic Course could not be located.
     *
     * @return a fully configured {@link ResourceNotFoundException} instance
     */
    public static ResourceNotFoundException courseNotFound() {
        return new ResourceNotFoundException(AcademicErrorCodes.COURSE_NOT_FOUND);
    }

    /**
     * Instantiates a standardized {@link DuplicateResourceException} indicating
     * that a Course with the specified name already exists in the system.
     *
     * @return a fully configured {@link DuplicateResourceException} instance
     */
    public static DuplicateResourceException courseAlreadyExists() {
        return new DuplicateResourceException(AcademicErrorCodes.COURSE_ALREADY_EXISTS);
    }

    /**
     * Instantiates a standardized {@link ResourceNotFoundException} indicating
     * that a requested Student enrollment record could not be located.
     *
     * @return a fully configured {@link ResourceNotFoundException} instance
     */
    public static ResourceNotFoundException studentNotFound() {
        return new ResourceNotFoundException(AcademicErrorCodes.STUDENT_NOT_FOUND);
    }

    /**
     * Instantiates a standardized {@link DuplicateResourceException} indicating
     * that a Student with the specified academic registration already exists in the system.
     *
     * @return a fully configured {@link DuplicateResourceException} instance
     */
    public static DuplicateResourceException studentAlreadyExists() {
        return new DuplicateResourceException(AcademicErrorCodes.STUDENT_ALREADY_EXISTS);
    }
}