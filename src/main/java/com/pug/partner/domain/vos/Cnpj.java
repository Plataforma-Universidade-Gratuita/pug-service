package com.pug.partner.domain.vos;

import com.pug.partner.domain.enums.PartnerErrorCodes;
import com.pug.shared.domain.DomainError;
import com.pug.shared.exceptions.AppValidationException;
import com.pug.shared.utils.StringUtils;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Value;

/**
 * Value object representing a Brazilian CNPJ (Cadastro Nacional da Pessoa Jurídica). Extends
 * DomainError to support deferred validation.
 */
@Getter
@Value
@EqualsAndHashCode(callSuper = false)
public class Cnpj extends DomainError {

  String value;

  @Builder(toBuilder = true)
  private Cnpj(String value) {
    this.value = value;
  }

  /**
   * Factory method to create a new Cnpj. It cleans the input (removes non-digits) and runs
   * validation.
   *
   * @param rawValue the CNPJ value as a string
   * @return The Cnpj instance (which may contain errors)
   */
  public static Cnpj factory(String rawValue) {
    String cleaned = StringUtils.isEmpty(rawValue) ? null : rawValue.replaceAll("\\D", "");

    Cnpj vo = Cnpj.builder().value(cleaned).build();
    vo.validate();
    return vo;
  }

  /** Validates the CNPJ, populating the problems list if invalid. */
  private void validate() {
    if (StringUtils.isEmpty(value)) {
      addError(new AppValidationException.Problem(PartnerErrorCodes.INVALID_CNPJ_BLANK));
      return;
    }

    if (value.length() != 14) {
      addError(new AppValidationException.Problem(PartnerErrorCodes.INVALID_CNPJ_LENGTH));
      return;
    }

    if (value.chars().distinct().count() == 1) {
      addError(new AppValidationException.Problem(PartnerErrorCodes.INVALID_CNPJ_FORMAT));
      return;
    }

    if (!isValidChecksum(value)) {
      addError(new AppValidationException.Problem(PartnerErrorCodes.INVALID_CNPJ_FORMAT));
    }
  }

  /**
   * Returns the formatted string representation of the CNPJ (e.g., "XX.XXX.XXX/XXXX-XX"). Returns
   * raw value if length is invalid.
   *
   * @return the formatted CNPJ as a String.
   */
  public String toFormattedString() {
    if (value == null || value.length() != 14) {
      return value;
    }
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

  @Override
  public String toString() {
    return value;
  }

  // --- Internal Validation Logic ---

  private static boolean isValidChecksum(String cnpj) {
    int d1 = calculateChecksumDigit(cnpj, new int[] {5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2});
    int d2 = calculateChecksumDigit(cnpj, new int[] {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2});
    return (cnpj.charAt(12) - '0') == d1 && (cnpj.charAt(13) - '0') == d2;
  }

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
