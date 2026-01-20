package com.pug.shared.exceptions;

import com.pug.shared.domain.enums.GenericErrorCodes;
import lombok.Getter;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Base class for application-specific exceptions. These exceptions encapsulate
 * an error code and optional details, allowing for consistent API error responses.
 */
public abstract class ApplicationException extends RuntimeException {
    @Getter
    private final GenericErrorCodes errorCode;
    private final Map<String, Object> details;

    /**
     * Constructs a new ApplicationException with the specified error code and details.
     *
     * @param errorCode the generic error code representing the type of error
     * @param details   additional details about the error (can be null)
     */
    protected ApplicationException(GenericErrorCodes errorCode, Map<String, Object> details) {
        super(errorCode.getBundleKey());
        this.errorCode = errorCode;
        this.details = details != null ? new HashMap<>(details) : new HashMap<>();
    }

    /**
     * Constructs a new ApplicationException with the specified error code and no details.
     *
     * @param errorCode the generic error code representing the type of error
     */
    protected ApplicationException(GenericErrorCodes errorCode) {
        this(errorCode, null);
    }

    /**
     * Returns an unmodifiable view of the details map.
     *
     * @return an unmodifiable map containing the error details
     */
    public Map<String, Object> getDetails() {
        return Collections.unmodifiableMap(details);
    }
}