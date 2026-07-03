/*
 * Copyright (c) 2026 Mateus Fernandes and Plataforma Universidade Gratuita.
 * All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized use,
 * copying, modification, distribution, or deployment is prohibited.
 */

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

  String value;

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
