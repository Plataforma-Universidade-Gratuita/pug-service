package br.org.catolicasc.pug.shared.domain;

import br.org.catolicasc.pug.shared.domain.enums.GenericFieldErrorCodes;
import br.org.catolicasc.pug.shared.domain.enums.SharedFieldErrorCodes;
import br.org.catolicasc.pug.shared.utils.StringUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * Abstract base class for domain entities or objects that require self-validation. It provides
 * mechanisms to accumulate, check, and retrieve field-specific domain errors without throwing
 * exceptions immediately, allowing for the collection of multiple validation failures.
 */
@Getter
public abstract class DomainError {

  @ToString.Exclude @EqualsAndHashCode.Exclude
  private final List<GenericFieldErrorCodes> fieldErrors = new ArrayList<>();

  /**
   * Retrieves a defensive copy of the accumulated validation field errors.
   *
   * @return a new {@link List} containing the current {@link GenericFieldErrorCodes}
   */
  public List<GenericFieldErrorCodes> getFieldErrors() {
    return new ArrayList<>(fieldErrors);
  }

  /**
   * Checks if there are any validation field errors accumulated in this domain object.
   *
   * @return {@code true} if there is at least one validation error, {@code false} otherwise
   */
  public boolean hasFieldErrors() {
    return !fieldErrors.isEmpty();
  }

  /**
   * Adds a single validation field error to the internal list of errors.
   *
   * @param fieldError the {@link GenericFieldErrorCodes} representing the validation failure to add
   */
  protected void addFieldError(GenericFieldErrorCodes fieldError) {
    fieldErrors.add(fieldError);
  }

  /**
   * Adds a collection of validation field errors to the internal list of errors.
   *
   * @param fieldErrors a {@link List} of {@link GenericFieldErrorCodes} to append to the current
   *     errors
   */
  protected void addFieldErrors(List<GenericFieldErrorCodes> fieldErrors) {
    this.fieldErrors.addAll(fieldErrors);
  }

  /**
   * Validates that the provided UUID identifier is not null.
   *
   * <p>If the validation fails, a {@link SharedFieldErrorCodes#INVALID_ID_BLANK} error is appended.
   *
   * @param id the {@link UUID} to validate
   */
  protected void validateIdField(UUID id) {
    if (id == null) {
      addFieldError(SharedFieldErrorCodes.INVALID_ID_BLANK);
    }
  }

  /**
   * Validates that the provided name string meets the standard domain constraints.
   *
   * <p>Rules applied:
   *
   * <ul>
   *   <li>Must not be null or empty (appends {@link SharedFieldErrorCodes#INVALID_NAME_BLANK})
   *   <li>Must not exceed 150 characters (appends {@link
   *       SharedFieldErrorCodes#INVALID_NAME_TOO_LONG})
   * </ul>
   *
   * @param name the string name to validate
   */
  protected void validateNameField(String name) {
    if (StringUtils.isEmpty(name)) {
      addFieldError(SharedFieldErrorCodes.INVALID_NAME_BLANK);
      return;
    }
    if (name.length() > 150) {
      addFieldError(SharedFieldErrorCodes.INVALID_NAME_TOO_LONG);
    }
  }

  /**
   * Returns a human-readable string summary of all accumulated validation field errors.
   *
   * <p>The summary is formatted as a comma-separated list in the pattern {@code
   * "bundleKey(fieldName)"}. This method is particularly useful for logging data integrity issues
   * and debugging.
   *
   * @return a formatted summary string of the errors, or {@code "No errors"} if the list is empty
   */
  public String getProblemsSummary() {
    if (fieldErrors.isEmpty()) {
      return "No errors";
    }
    return fieldErrors.stream()
        .map(
            gfec -> {
              String key = gfec.getBundleKey();
              String field = gfec.getFieldName();
              return key + "(" + field + ")";
            })
        .collect(Collectors.joining(", "));
  }
}
