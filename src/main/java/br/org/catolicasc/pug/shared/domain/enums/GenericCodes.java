package br.org.catolicasc.pug.shared.domain.enums;

/**
 * Interface representing a general business, system, or cross-cutting message code.
 *
 * <p>Unlike {@link GenericFieldErrorCodes}, this interface is used for high-level codes that are
 * not necessarily bound to a single specific field (e.g., general error messages, system statuses,
 * or business rule violations) or general enums of the system. It ensures these codes can be
 * properly internationalized across the application.
 */
public interface GenericCodes {

  /**
   * Retrieves the unique key used to look up the localized message in the application's resource
   * bundles (e.g., {@code messages_en_US.properties}).
   *
   * @return the i18n bundle key as a {@link String}
   */
  String getBundleKey();

  /**
   * Retrieves a string representation of the code itself.
   *
   * <p>This is particularly useful for API responses where a raw string identifier (e.g.,
   * "RESOURCE_NOT_FOUND") is needed alongside the localized message. By default, if the
   * implementing instance is an {@link Enum}, it returns the enum's {@code name()}. If it is a
   * standard class, it falls back to the class's simple name. Implementers can override this method
   * to provide a custom string code if necessary.
   *
   * @return the string identifier of the code
   */
  default String getCode() {
    if (this instanceof Enum<?>) {
      return ((Enum<?>) this).name();
    }
    return this.getClass().getSimpleName();
  }
}
