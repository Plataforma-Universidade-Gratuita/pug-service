package com.pug.partner.domain.vos;

import com.pug.partner.domain.enums.PartnerErrorCodes;
import com.pug.shared.utils.StringUtils;
import org.jetbrains.annotations.NotNull;

/**
 * Value object representing a Brazilian CNPJ (Cadastro Nacional da Pessoa Jurídica).
 *
 * <p>Also contains a method to format the CNPJ in the standard pattern XX.XXX.XXX/XXXX-XX
 *
 * @param value the CNPJ value as a string of digits
 */
public record Cnpj(String value) {

  /**
   * Constructs a Cnpj value object after validating the input.
   *
   * @param value the CNPJ value as a string
   * @throws AppValidationException if the CNPJ is null, has an invalid length, contains non-digit
   *     characters, or fails the checksum validation
   */
  public Cnpj {
    String digits = sanitize(value);
    if (StringUtils.isEmpty(digits)) {
      throw new AppValidationException(PartnerErrorCodes.INVALID_CNPJ_BLANK);
    }
    if (digits.length() != 14) {
      throw new AppValidationException(PartnerErrorCodes.INVALID_CNPJ_LENGTH);
    }
    if (!digits.chars().allMatch(Character::isDigit) || !isValid(digits)) {
      throw new AppValidationException(PartnerErrorCodes.INVALID_CNPJ_FORMAT);
    }
    value = digits;
  }

  /**
   * Sanitizes the input string by removing all non-digit characters.
   *
   * @param s the input string
   * @return a string containing only digits, or null if the input was null
   */
  public static String sanitize(String s) {
    if (s == null) {
      return null;
    }
    return s.replaceAll("\\D", "");
  }

  /**
   * Validates the CNPJ using its checksum algorithm.
   *
   * @param cnpj the CNPJ string containing only digits
   * @return true if the CNPJ is valid, false otherwise
   */
  private static boolean isValid(String cnpj) {
    if (cnpj.chars().distinct().count() == 1) {
      return false;
    }

    int d1 = checksum(cnpj, new int[] {5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2});
    int d2 = checksum(cnpj, new int[] {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2});
    return (cnpj.charAt(12) - '0') == d1 && (cnpj.charAt(13) - '0') == d2;
  }

  /**
   * Calculates the checksum digit for the CNPJ.
   *
   * @param cnpj the CNPJ string containing only digits
   * @param weights the weights used in the checksum calculation
   * @return the calculated checksum digit
   */
  private static int checksum(String cnpj, int[] weights) {
    int sum = 0;
    for (int i = 0; i < weights.length; i++) {
      sum += (cnpj.charAt(i) - '0') * weights[i];
    }
    int mod = sum % 11;
    int digit = 11 - mod;
    return digit >= 10 ? 0 : digit;
  }

  /**
   * Returns the string representation of the CNPJ.
   *
   * @return the CNPJ value as a string
   */
  @Override
  public @NotNull String toString() {
    return value;
  }
}
