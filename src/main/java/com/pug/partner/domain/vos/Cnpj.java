package com.pug.partner.domain.vos;

import com.pug.partner.domain.enums.PartnerErrorCodes;
import com.pug.shared.domain.DomainError;
import com.pug.shared.domain.Problem;
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
    vo.collectValidationProblems();
    return vo;
  }

  /** Validates the CNPJ, populating the problems list if invalid. */
  private void collectValidationProblems() {
    validateStringField(value, 14L, "cnpj");
    if (value.chars().distinct().count() == 1) {
      addError(new Problem(PartnerErrorCodes.INVALID_CNPJ_FORMAT));
      return;
    }
    if (!isValidChecksum(value)) {
      addError(new Problem(PartnerErrorCodes.INVALID_CNPJ_FORMAT));
    }
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
