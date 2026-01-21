package com.pug.identity.domain.vos;

import com.pug.identity.domain.enums.IdentityErrorCodes;
import com.pug.shared.exceptions.AppValidationException;
import com.pug.shared.utils.StringUtils;
import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.NotNull;

/**
 * Value object representing a Brazilian CPF (Cadastro de Pessoas Físicas).
 *
 * <p>Also includes a method to return the formatted version of the CPF.
 *
 * @param value the CPF as a String (11 digits, unformatted)
 */
public record Cpf(String value) {

  /**
   * Constructs a Cpf value object and validates the input.
   *
   * @param value the CPF as a String
   * @throws AppValidationException if the CPF is null, isn't exactly 11 digits long, has the same
   *     digit repeated or has invalid check digits. This exception may contain multiple validation
   *     problems.
   */
  public Cpf {
    List<AppValidationException.Problem> problems = new ArrayList<>();
    String cleanedDigits = null;

    // TODO: Consider replacing this manual CPF validation logic with a respected library
    //  like Caelum Stella Core (br.com.caelum.stella:stella-core) in the future.
    //  This will improve robustness and maintainability.

    if (StringUtils.isEmpty(value)) {
      problems.add(new AppValidationException.Problem(IdentityErrorCodes.INVALID_CPF_BLANK, "cpf"));
    } else {
      cleanedDigits = value.replaceAll("\\D", "");
      if (cleanedDigits.length() != 11) {
        problems.add(
            new AppValidationException.Problem(IdentityErrorCodes.INVALID_CPF_LENGTH, "cpf"));
      } else {
        if (allSameDigit(cleanedDigits)) {
          problems.add(
              new AppValidationException.Problem(IdentityErrorCodes.INVALID_CPF_FORMAT, "cpf"));
        }
        if (!validCheckDigits(cleanedDigits)) {
          problems.add(
              new AppValidationException.Problem(IdentityErrorCodes.INVALID_CPF_FORMAT, "cpf"));
        }
      }
    }

    if (!problems.isEmpty()) {
      throw new AppValidationException(problems);
    }

    value = cleanedDigits;
  }

  /**
   * Checks if all characters in the string are the same digit.
   *
   * @param s the string to check
   * @return true if all characters are the same, false otherwise
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

  /**
   * Returns the string representation of the CPF (11 digits, unformatted).
   *
   * @return the CPF as a String
   */
  @Override
  public @NotNull String toString() {
    return value;
  }

  /**
   * Returns the formatted string representation of the CPF (e.g., "123.456.789-00").
   *
   * @return the formatted CPF as a String.
   */
  public String toFormattedString() {
    return value.substring(0, 3)
        + "."
        + value.substring(3, 6)
        + "."
        + value.substring(6, 9)
        + "-"
        + value.substring(9, 11);
  }
}
