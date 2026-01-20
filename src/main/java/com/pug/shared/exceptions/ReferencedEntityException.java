package com.pug.shared.exceptions;

import com.pug.shared.domain.enums.GenericErrorCodes;

import java.util.Map;

/**
 * Exception thrown when an entity cannot be deleted or modified because it is referenced by other entities.
 */
public class ReferencedEntityException extends ApplicationException {
    /**
     * Constructor for ReferencedEntityException.
     *
     * @param errorCode The specific error code representing the referenced entity error.
     */
    public ReferencedEntityException(GenericErrorCodes errorCode) {
        super(errorCode);
    }

    /**
     * Constructor for ReferencedEntityException with additional details.
     *
     * @param errorCode The specific error code representing the referenced entity error.
     * @param details   A map containing additional details about the error.
     */
    public ReferencedEntityException(GenericErrorCodes errorCode, Map<String, Object> details) {
        super(errorCode, details);
    }
}