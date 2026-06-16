package br.org.catolicasc.pug.shared.exceptions;

import br.org.catolicasc.pug.shared.domain.enums.GenericCodes;
import lombok.Getter;

/**
 * Exception thrown when a syntactically valid request violates a specific, high-level business rule
 * or domain policy.
 *
 * <p>Unlike {@link AppValidationException}, which is used for granular field-level constraints
 * (like "name cannot be blank"), this exception is used for aggregate-level state violations or
 * cross-cutting rules (e.g., "Cannot delete a default city" or "User is already enrolled").
 *
 * <p>It typically maps to an HTTP 422 (Unprocessable Entity) or HTTP 409 (Conflict) response,
 * returning a single, localized message to the client without field-specific mappings.
 */
@Getter
public class BusinessRuleException extends RuntimeException {

  private final GenericCodes code;

  /**
   * Constructs a new {@code BusinessRuleException}.
   *
   * @param code The specific {@link GenericCodes} representing the rule violation (e.g., {@code
   *     GeoErrorCodes.CITY_IS_DEFAULT}).
   */
  public BusinessRuleException(GenericCodes code) {
    super(code.getBundleKey());
    this.code = code;
  }
}
