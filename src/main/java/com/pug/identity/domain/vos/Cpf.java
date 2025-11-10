package com.pug.identity.domain.vos;

import com.pug.identity.domain.enums.IdentityErrorCodes;
import com.pug.shared.exceptions.AppValidationException;
import org.jetbrains.annotations.NotNull;

/**
 * CPF value object. Accepts formatted input, stores only 11 digits, validates check digits.
 *
 * @param value the CPF as a String.
 */
public record Cpf(String value) {

  /**
   * Constructs a Cpf value object and validates the input.
   *
   * @param value the CPF as a String.
   */
  public Cpf {
    if (value == null) {
      throw new AppValidationException(IdentityErrorCodes.INVALID_CPF);
    }
    String digits = value.replaceAll("\\D", "");
    if (digits.length() != 11) {
      throw new AppValidationException(IdentityErrorCodes.INVALID_CPF);
    }
    if (allSameDigit(digits)) {
      throw new AppValidationException(IdentityErrorCodes.INVALID_CPF);
    }
    if (!validCheckDigits(digits)) {
      throw new AppValidationException(IdentityErrorCodes.INVALID_CPF);
    }
    value = digits;
  }

  /**
   * Checks if all characters in the string are the same digit.
   *
   * @param s the string to check.
   * @return true if all characters are the same, false otherwise.
   */
  private static boolean allSameDigit(String s) {
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
   * @param s the string with 11 digits.
   * @return true if check digits are valid, false otherwise.
   */
  private static boolean validCheckDigits(String s) {
    int d1 = calcDigit(s, 9);
    int d2 = calcDigit(s, 10);
    return (s.charAt(9) - '0') == d1 && (s.charAt(10) - '0') == d2;
  }

  /**
   * Calculates a CPF check digit.
   *
   * @param s the string with digits.
   * @param len number of digits to use for calculation (9 or 10).
   * @return the calculated check digit.
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

  /**
   * Returns the string representation of the CPF (11 digits).
   *
   * @return the CPF as a String.
   */
  @Override
  public @NotNull String toString() {
    return value;
  }

  /**
   * Returns the formatted representation of the CPF (xxx.xxx.xxx-xx).
   *
   * @return the formatted CPF as a String.
   */
  public String formatted() {
    String v = value;
    return v.substring(0, 3)
        + "."
        + v.substring(3, 6)
        + "."
        + v.substring(6, 9)
        + "-"
        + v.substring(9);
  }
}
