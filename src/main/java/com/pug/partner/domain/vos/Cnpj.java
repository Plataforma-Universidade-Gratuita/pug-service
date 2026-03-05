package com.pug.partner.domain.vos;

import com.pug.partner.domain.enums.PartnerFieldErrorCodes;
import com.pug.shared.domain.DomainError;
import com.pug.shared.utils.StringUtils;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Value;

/**
 * Immutable Value Object (VO) representing a Brazilian CNPJ (Cadastro Nacional da Pessoa Jurídica).
 *
 * <p>Extends {@link DomainError} to encapsulate and accumulate domain validation rules specific to
 * Brazilian corporate taxpayer registry numbers without throwing immediate exceptions. This class
 * inherently handles formatting variations by sanitizing the input prior to validation.
 */
@Getter
@Value
@EqualsAndHashCode(callSuper = false)
public class Cnpj extends DomainError {

  /** The raw, numeric-only 14-digit string representing the CNPJ. */
  String value;

  /**
   * Constructs a {@code Cnpj} instance.
   *
   * @param value the sanitized, numeric-only CNPJ string
   */
  @Builder(toBuilder = true)
  private Cnpj(String value) {
    this.value = value;
  }

  /**
   * Factory method to create a new {@code Cnpj} instance.
   *
   * <p>The provided raw value is automatically sanitized (all non-numeric characters stripped)
   * before instantiation. The instance is immediately self-validated. Any validation failures are
   * accumulated internally and can be retrieved via {@link #getFieldErrors()}.
   *
   * @param rawValue the raw CNPJ string (formatted with punctuation or unformatted)
   * @return a self-validated {@link Cnpj} instance
   */
  public static Cnpj factory(String rawValue) {
    String cleaned = StringUtils.isEmpty(rawValue) ? null : rawValue.replaceAll("\\D", "");

    Cnpj vo = Cnpj.builder().value(cleaned).build();
    vo.collectValidationProblems();
    return vo;
  }

  /**
   * Evaluates internal constraints and accumulates validation problems.
   *
   * <p>Business rules applied:
   *
   * <ul>
   *   <li>Must not be null or empty (appends {@link PartnerFieldErrorCodes#INVALID_CNPJ_BLANK})
   *   <li>Must be exactly 14 digits long, cannot consist of the same repeated digit, and must pass
   *       the standard modulo-11 checksum algorithm for both verification digits (appends {@link
   *       PartnerFieldErrorCodes#INVALID_CNPJ_FORMAT})
   * </ul>
   */
  private void collectValidationProblems() {
    if (StringUtils.isEmpty(value)) {
      addFieldError(PartnerFieldErrorCodes.INVALID_CNPJ_BLANK);
      return;
    }
    if (value.length() != 14 || value.chars().distinct().count() == 1 || !isValidChecksum(value)) {
      addFieldError(PartnerFieldErrorCodes.INVALID_CNPJ_FORMAT);
    }
  }

  /* --- Internal Validation Logic --- */

  /**
   * Executes the standard Brazilian modulo-11 checksum algorithm to validate the last two digits
   * (Verification Digits) of the CNPJ.
   *
   * @param cnpj the 14-digit numeric string representing the CNPJ
   * @return {@code true} if the calculated check digits match the provided string, {@code false}
   *     otherwise
   */
  private static boolean isValidChecksum(String cnpj) {
    int d1 = calculateChecksumDigit(cnpj, new int[] {5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2});
    int d2 = calculateChecksumDigit(cnpj, new int[] {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2});
    return (cnpj.charAt(12) - '0') == d1 && (cnpj.charAt(13) - '0') == d2;
  }

  /**
   * Calculates a single CNPJ verification digit based on the modulo-11 algorithm and provided
   * weights.
   *
   * @param cnpj the numeric string containing the base digits for calculation
   * @param weights the specific array of integer multipliers required for the respective
   *     verification digit
   * @return the mathematically calculated verification digit (0-9)
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
}
