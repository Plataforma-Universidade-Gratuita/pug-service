/*
 * Copyright (c) 2026 Mateus Fernandes and Plataforma Universidade Gratuita.
 * All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized use,
 * copying, modification, distribution, or deployment is prohibited.
 */

package br.org.catolicasc.pug.partner.domain.vos;

import br.com.caelum.stella.validation.CNPJValidator;
import br.org.catolicasc.pug.partner.domain.enums.PartnerFieldErrorCodes;
import br.org.catolicasc.pug.shared.domain.DomainError;
import br.org.catolicasc.pug.shared.utils.StringUtils;
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

  String value;

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

  private void collectValidationProblems() {
    if (StringUtils.isEmpty(value)) {
      addFieldError(PartnerFieldErrorCodes.INVALID_CNPJ_BLANK);
      return;
    }
    CNPJValidator validator = new CNPJValidator(false);
    try {
      validator.assertValid(value);
    } catch (Exception e) {
      addFieldError(PartnerFieldErrorCodes.INVALID_CNPJ_FORMAT);
    }
  }
}
