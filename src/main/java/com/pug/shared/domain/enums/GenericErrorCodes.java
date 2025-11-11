package com.pug.shared.domain.enums;

/**
 * Interface for generic error codes.
 *
 * <p>Any enumeration representing error codes should implement this interface
 * to ensure consistency in accessing the associated bundle keys.</p>
 */
public interface GenericErrorCodes {
  String getBundleKey();
}
