package com.pug.shared.domain;

import com.pug.shared.domain.enums.SharedErrorCodes;
import com.pug.shared.utils.StringUtils;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Abstract base class for domain errors that can accumulate validation problems.
 */
@Getter
public abstract class DomainError {

  @ToString.Exclude
  @EqualsAndHashCode.Exclude
  private final List<Problem> problems = new ArrayList<>();

  /**
   * Returns a defensive copy of the validation problems.
   *
   * @return a list of problems
   */
  public List<Problem> getProblems() {
    return new ArrayList<>(problems);
  }

  /**
   * Checks if there are any validation errors.
   *
   * @return true if there are errors, false otherwise
   */
  public boolean hasErrors() {
    return !problems.isEmpty();
  }

  /**
   * Adds a validation problem to the list of problems.
   *
   * @param problem the validation problem to add
   */
  protected void addError(Problem problem) {
    problems.add(problem);
  }

  /**
   * Adds multiple validation problems to the list of problems.
   *
   * @param newProblems the list of validation problems to add
   */
  protected void addErrors(List<Problem> newProblems) {
    problems.addAll(newProblems);
  }

  /**
   * Validates that the given UUID id is not null.
   *
   * @param id the UUID to validate
   */
  protected void validateIdField(UUID id) {
    if (id == null) {
      addError(new Problem(SharedErrorCodes.INVALID_ID_BLANK));
    }
  }

  /**
   * Validates that the given UUID foreign key id is not null.
   *
   * @param id        the UUID to validate
   * @param fieldName the name of the foreign key field being validated (used for error messages)
   */
  protected void validateForeignKeyField(UUID id, String fieldName) {
    if (id == null) {
      addError(new Problem(SharedErrorCodes.INVALID_FOREIGN_KEY_BLANK, fieldName));
    }
  }

  /**
   * Validates that the given string field is not null or empty, and optionally checks its length.
   *
   * @param value     the string value to validate
   * @param length    the maximum allowed length of the string (null if no length check)
   * @param fieldName the name of the field being validated (used for error messages)
   */
  protected void validateStringField(String value, Long length, String fieldName) {
    if (StringUtils.isEmpty(value)) {
      addError(new Problem(SharedErrorCodes.INVALID_FIELD_BLANK, fieldName));
    } else if (length != null && value.length() > length.intValue()) {
      addError(new Problem(SharedErrorCodes.INVALID_FIELD_LENGTH, fieldName));
    }
  }

  /**
   * Validates that the given BigDecimal field is not null and optionally checks if it is negative or zero.
   *
   * @param value           the BigDecimal value to validate
   * @param fieldName       the name of the field being validated (used for error messages)
   * @param negativeAllowed whether negative values are allowed
   * @param zeroAllowed     whether zero values are allowed
   */
  protected void validateBigDecimalField(BigDecimal value, String fieldName, boolean negativeAllowed, boolean zeroAllowed) {
    if (value == null) {
      addError(new Problem(SharedErrorCodes.INVALID_FIELD_BLANK, fieldName));
    } else if (!negativeAllowed && value.signum() < 0) {
      addError(new Problem(SharedErrorCodes.INVALID_NUMBER_NEGATIVE, fieldName));
    } else if (!zeroAllowed && value.signum() == 0) {
      addError(new Problem(SharedErrorCodes.INVALID_NUMBER_ZERO, fieldName));
    }
  }

  /**
   * Validates that the given LocalDate fields are not null and that the due date is not before the start date.
   *
   * @param startDate the start date to validate
   * @param dueDate   the due date to validate
   */
  protected void validateDateFields(LocalDate startDate, LocalDate dueDate) {
    if (startDate == null) {
      addError(new Problem(SharedErrorCodes.INVALID_FIELD_BLANK, "startDate"));
    }
    if (dueDate == null) {
      addError(new Problem(SharedErrorCodes.INVALID_FIELD_BLANK, "dueDate"));
    }
    if (startDate != null && dueDate != null && dueDate.isBefore(startDate)) {
      addError(new Problem(SharedErrorCodes.INVALID_PERIOD_RANGE, "period"));
    }
  }

  /**
   * Validates the audited fields (createdAt and updatedAt) for null values and logical consistency.
   *
   * @param createdAt the creation timestamp to validate
   * @param updatedAt the update timestamp to validate
   */
  protected void validateAuditedFields(OffsetDateTime createdAt, OffsetDateTime updatedAt) {
    if (createdAt == null) {
      addError(new Problem(SharedErrorCodes.INVALID_CREATED_AT_BLANK));
    }
    if (updatedAt == null) {
      addError(new Problem(SharedErrorCodes.INVALID_UPDATED_AT_BLANK));
    }
    if (createdAt != null && updatedAt != null && updatedAt.isBefore(createdAt)) {
      addError(new Problem(SharedErrorCodes.INVALID_UPDATED_AT_BEFORE_CREATED));
    }
  }

  /**
   * Returns a readable string summary of all validation problems.
   *
   * <p>Useful for logging data integrity issues.
   */
  public String getProblemsSummary() {
    if (problems.isEmpty()) {
      return "No errors";
    }
    return problems.stream()
            .map(
                    p -> {
                      String key = p.code().getBundleKey();
                      String field = p.code().getFieldName();
                      return field != null ? key + "(" + field + ")" : key;
                    })
            .collect(Collectors.joining(", "));
  }
}
