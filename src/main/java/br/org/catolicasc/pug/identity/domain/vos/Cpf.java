package br.org.catolicasc.pug.identity.domain.vos;

import br.org.catolicasc.pug.identity.domain.enums.IdentityFieldErrorCodes;
import br.org.catolicasc.pug.shared.domain.DomainError;
import br.org.catolicasc.pug.shared.utils.StringUtils;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Value;

/**
 * Immutable Value Object (VO) representing a Brazilian CPF (Cadastro de Pessoas Físicas).
 *
 * <p>Extends {@link DomainError} to encapsulate and accumulate domain validation rules specific to
 * Brazilian individual taxpayer registry numbers without throwing immediate exceptions. This class
 * inherently handles formatting variations by sanitizing the input prior to validation.
 */
@Getter
@Value
@EqualsAndHashCode(callSuper = false)
public class Cpf extends DomainError {

  /** The raw, numeric-only 11-digit string representing the CPF. */
  String value;

  /**
   * Constructs a {@code Cpf} instance.
   *
   * @param value the sanitized, numeric-only CPF string
   */
  @Builder(toBuilder = true)
  private Cpf(String value) {
    this.value = value;
  }

  /**
   * Factory method to create a new {@code Cpf} instance.
   *
   * <p>The provided raw value is automatically sanitized (all non-numeric characters stripped)
   * before instantiation. The instance is immediately self-validated. Any validation failures are
   * accumulated internally and can be retrieved via {@link #getFieldErrors()}.
   *
   * @param rawValue the raw CPF string (formatted with punctuation or unformatted)
   * @return a self-validated {@link Cpf} instance
   */
  public static Cpf factory(String rawValue) {
    String cleaned = StringUtils.isEmpty(rawValue) ? null : rawValue.replaceAll("\\D", "");

    Cpf vo = Cpf.builder().value(cleaned).build();
    vo.collectValidationProblems();
    return vo;
  }

  /**
   * Evaluates internal constraints and accumulates validation problems.
   *
   * <p>Business rules applied:
   *
   * <ul>
   *   <li>Must not be null or empty (appends {@link IdentityFieldErrorCodes#INVALID_CPF_BLANK})
   *   <li>Must be exactly 11 digits long, cannot consist of the same repeated digit, and must pass
   *       the standard modulo-11 checksum algorithm for both verification digits (appends {@link
   *       IdentityFieldErrorCodes#INVALID_CPF_FORMAT})
   * </ul>
   */
  private void collectValidationProblems() {
    if (StringUtils.isEmpty(value)) {
      addFieldError(IdentityFieldErrorCodes.INVALID_CPF_BLANK);
      return;
    }
    if (value.length() != 11 || allSameDigit(value) || !validCheckDigits(value)) {
      addFieldError(IdentityFieldErrorCodes.INVALID_CPF_FORMAT);
    }
  }

  // --- Internal Validation Logic ---

  /**
   * Evaluates if all characters within the provided string are identical.
   *
   * <p>This is a requirement for CPF validation, as strings like "11111111111" pass the
   * mathematical checksum but are structurally invalid CPFs.
   *
   * @param s the numeric string to evaluate
   * @return {@code true} if all characters are the same repeated digit, {@code false} otherwise
   */
  private static boolean allSameDigit(String s) {
    if (StringUtils.isEmpty(s)) {
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
   * Executes the standard Brazilian modulo-11 checksum algorithm to validate the last two digits
   * (Verification Digits) of the CPF.
   *
   * @param s the 11-digit numeric string representing the CPF
   * @return {@code true} if the calculated check digits match the provided string, {@code false}
   *     otherwise
   */
  private static boolean validCheckDigits(String s) {
    int d1 = calcDigit(s, 9);
    int d2 = calcDigit(s, 10);
    return (s.charAt(9) - '0') == d1 && (s.charAt(10) - '0') == d2;
  }

  /**
   * Calculates a single CPF verification digit based on the modulo-11 algorithm.
   *
   * @param s the numeric string containing the base digits for calculation
   * @param len the number of digits to consider (9 for the first verification digit, 10 for the
   *     second)
   * @return the mathematically calculated verification digit (0-9)
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
