package com.pug.identity.domain.vos;

import com.pug.identity.domain.enums.IdentityErrorCodes;
import com.pug.shared.domain.DomainError;
import com.pug.shared.exceptions.AppValidationException;
import com.pug.shared.utils.StringUtils;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Value;

/**
 * Value object representing a Brazilian CPF (Cadastro de Pessoas Físicas). Converted to class to
 * extend DomainError, allowing deferred validation.
 */
@Getter
@Value
@EqualsAndHashCode(callSuper = false)
public class Cpf extends DomainError {

  String value;

  @Builder(toBuilder = true)
  private Cpf(String value) {
    this.value = value;
  }

  /**
   * Factory method to create a new CPF. It cleans the input (removes non-digits) and runs
   * validation. It does not throw exceptions immediately but collects them in the problems list.
   *
   * @param rawValue The raw CPF string (formatted or unformatted)
   * @return The Cpf instance (which may contain errors)
   */
  public static Cpf factory(String rawValue) {
    String cleaned = StringUtils.isEmpty(rawValue) ? null : rawValue.replaceAll("\\D", "");

    Cpf vo = Cpf.builder().value(cleaned).build();
    vo.collectValidationProblems();
    return vo;
  }

  /** Validates the CPF format and digits, populating the problems list if invalid. */
  private void collectValidationProblems() {
    if (StringUtils.isEmpty(value)) {
      addError(new AppValidationException.Problem(IdentityErrorCodes.INVALID_CPF_BLANK));
      return;
    }

    if (value.length() != 11) {
      addError(new AppValidationException.Problem(IdentityErrorCodes.INVALID_CPF_LENGTH));
      return;
    }

    if (allSameDigit(value) || !validCheckDigits(value)) {
      addError(new AppValidationException.Problem(IdentityErrorCodes.INVALID_CPF_FORMAT));
    }
  }

  /**
   * Returns the formatted string representation of the CPF (e.g., "123.456.789-00"). Returns the
   * raw value if the CPF does not have the correct length (e.g., invalid state).
   *
   * @return the formatted CPF as a String.
   */
  public String toFormattedString() {
    if (value == null || value.length() != 11) {
      return value;
    }
    return value.substring(0, 3)
        + "."
        + value.substring(3, 6)
        + "."
        + value.substring(6, 9)
        + "-"
        + value.substring(9, 11);
  }

  /**
   * Returns the raw string representation of the CPF.
   *
   * @return the raw CPF as a String.
   */
  @Override
  public String toString() {
    return value;
  }

  // --- Internal Validation Logic ---

  /**
   * Checks if all characters in the string are the same digit.
   *
   * @param s the string to check
   * @return true if all characters are the same, false otherwise
   */
  private static boolean allSameDigit(String s) {
    if (s == null || s.isEmpty()) {
      return false;
    }
    char c = s.charAt(0);
    for (int i = 1; i < s.length(); i++) {
      if (s.charAt(i) != c) {
        return false;
      }
    }
    return true;
  }

  /**
   * Validates the CPF check digits.
   *
   * @param s the string with 11 digits
   * @return true if check digits are valid, false otherwise
   */
  private static boolean validCheckDigits(String s) {
    int d1 = calcDigit(s, 9);
    int d2 = calcDigit(s, 10);
    return (s.charAt(9) - '0') == d1 && (s.charAt(10) - '0') == d2;
  }

  /**
   * Calculates a CPF check digit.
   *
   * @param s the string with digits
   * @param len number of digits to use for calculation (9 or 10)
   * @return the calculated check digit
   */
  private static int calcDigit(String s, int len) {
    int sum = 0;
    for (int i = 0; i < len; i++) {
      int num = s.charAt(i) - '0';
      sum += num * (len + 1 - i);
    }
    int mod = sum % 11;
    return (mod < 2) ? 0 : 11 - mod;
  }
}
