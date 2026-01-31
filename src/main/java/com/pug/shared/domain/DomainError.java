package com.pug.shared.domain;

import com.pug.shared.exceptions.AppValidationException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
public abstract class DomainError {

  @ToString.Exclude @EqualsAndHashCode.Exclude
  private final List<AppValidationException.Problem> problems = new ArrayList<>();

  /**
   * Returns a defensive copy of the validation problems.
   *
   * @return a list of problems
   */
  public List<AppValidationException.Problem> getProblems() {
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
  protected void addError(AppValidationException.Problem problem) {
    problems.add(problem);
  }

  /**
   * Adds multiple validation problems to the list of problems.
   *
   * @param newProblems the list of validation problems to add
   */
  protected void addErrors(List<AppValidationException.Problem> newProblems) {
    problems.addAll(newProblems);
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
