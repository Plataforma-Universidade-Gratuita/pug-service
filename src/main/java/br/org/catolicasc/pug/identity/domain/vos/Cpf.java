package br.org.catolicasc.pug.identity.domain.vos;

import br.com.caelum.stella.validation.CPFValidator;
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

  /**
   * The raw, numeric-only 11-digit string representing the CPF.
   */
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
    CPFValidator validator = new CPFValidator(false);
    try {
      validator.assertValid(value);
    } catch (Exception e) {
      addFieldError(IdentityFieldErrorCodes.INVALID_CPF_FORMAT);
    }
  }
}
