package com.pug.partner.domain.vos;

import com.pug.partner.domain.enums.PartnerErrorCodes;
import com.pug.shared.exceptions.AppValidationException;
import com.pug.shared.utils.StringUtils;
import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.NotNull;

/**
 * Value object representing a Brazilian CNPJ (Cadastro Nacional da Pessoa Jurídica).
 *
 * <p>Also contains a method to format the CNPJ in the standard pattern XX.XXX.XXX/XXXX-XX
 *
 * @param value the CNPJ value as a string of digits (14 digits, unformatted)
 */
public record Cnpj(String value) {

  /**
   * Constructs a Cnpj value object after validating the input.
   *
   * @param value the CNPJ value as a string
   * @throws AppValidationException if the CNPJ is null, has an invalid length, contains non-digit
   *     characters, fails the checksum validation, or has multiple problems.
   */
  public Cnpj {
    List<AppValidationException.Problem> problems = new ArrayList<>();
    String cleanedDigits = null;

    // TODO: Consider replacing this manual CNPJ validation logic with a respected library
    //  like Caelum Stella Core (br.com.caelum.stella:stella-core) in the future.
    //  This will improve robustness and maintainability.

    if (StringUtils.isEmpty(value)) {
      problems.add(
          new AppValidationException.Problem(PartnerErrorCodes.INVALID_CNPJ_BLANK, "cnpj"));
    } else {
      cleanedDigits = value.replaceAll("\\D", "");

      if (cleanedDigits.length() != 14) {
        problems.add(
            new AppValidationException.Problem(PartnerErrorCodes.INVALID_CNPJ_LENGTH, "cnpj"));
      } else {
        if (cleanedDigits.chars().distinct().count() == 1) {
          problems.add(
              new AppValidationException.Problem(PartnerErrorCodes.INVALID_CNPJ_FORMAT, "cnpj"));
        }
        if (!isValidChecksum(cleanedDigits)) {
          problems.add(
              new AppValidationException.Problem(PartnerErrorCodes.INVALID_CNPJ_FORMAT, "cnpj"));
        }
      }
    }

    if (!problems.isEmpty()) {
      throw new AppValidationException(problems);
    }

    value = cleanedDigits;
  }

  /**
   * Validates the CNPJ using its checksum algorithm. (Original method renamed to reflect it's only
   * checksum)
   *
   * @param cnpj the CNPJ string containing only digits
   * @return true if the CNPJ checksums are valid, false otherwise
   */
  private static boolean isValidChecksum(String cnpj) {
    int d1 = calculateChecksumDigit(cnpj, new int[] {5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2});
    int d2 = calculateChecksumDigit(cnpj, new int[] {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2});
    return (cnpj.charAt(12) - '0') == d1 && (cnpj.charAt(13) - '0') == d2;
  }

  /**
   * Calculates a CNPJ checksum digit. (Original method renamed to be more descriptive)
   *
   * @param cnpj the CNPJ string containing only digits
   * @param weights the weights used in the checksum calculation
   * @return the calculated checksum digit
   */
  private static int calculateChecksumDigit(String cnpj, int[] weights) {
    int sum = 0;
    for (int i = 0; i < weights.length; i++) {
      sum += (cnpj.charAt(i) - '0') * weights[i];
    }
    int mod = sum % 11;
    int digit = 11 - mod;
    return digit >= 10 ? 0 : digit;
  }

  /**
   * Returns the string representation of the CNPJ (14 digits, unformatted).
   *
   * @return the CNPJ value as a string
   */
  @Override
  public @NotNull String toString() {
    return value;
  }

  /**
   * Returns the formatted string representation of the CNPJ (e.g., "XX.XXX.XXX/XXXX-XX").
   *
   * @return the formatted CNPJ as a String.
   */
  public String toFormattedString() {
    return value.substring(0, 2)
        + "."
        + value.substring(2, 5)
        + "."
        + value.substring(5, 8)
        + "/"
        + value.substring(8, 12)
        + "-"
        + value.substring(12, 14);
  }
}
