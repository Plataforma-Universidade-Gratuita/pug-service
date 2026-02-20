package com.pug.shared.domain.enums;

/** Interface for generic error codes with internationalization support. */
public interface GenericErrorCodes {
  /**
   * Retrieves the key for the error message bundle, used for internationalization.
   *
   * @return the bundle key as a String.
   */
  String getBundleKey();

  /**
   * Retrieves the name of the field associated with the error code, if applicable.
   *
   * @return the field name or null if no field is associated.
   */
  String getFieldName();
}
