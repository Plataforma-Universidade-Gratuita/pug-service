/*
 * Copyright (c) 2026 Mateus Fernandes and Plataforma Universidade Gratuita.
 * All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized use,
 * copying, modification, distribution, or deployment is prohibited.
 */

package br.org.catolicasc.pug.academic.domain.vos;

import br.org.catolicasc.pug.academic.domain.enums.AcademicFieldErrorCodes;
import br.org.catolicasc.pug.shared.domain.DomainError;
import br.org.catolicasc.pug.shared.utils.StringUtils;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Value;

/**
 * Immutable Value Object (VO) representing a formerStudent's Academic Registration identifier.
 *
 * <p>Extends {@link DomainError} to encapsulate and accumulate domain validation rules specific to
 * university registration formats without throwing immediate exceptions.
 */
@Getter
@Value
@EqualsAndHashCode(callSuper = false)
public class AcademicRegistration extends DomainError {

  String value;

  @Builder(toBuilder = true)
  private AcademicRegistration(String value) {
    this.value = value;
  }

  /**
   * Factory method to create a new {@code AcademicRegistration} instance.
   *
   * <p>Automatically trims whitespace from the input and executes validation logic.
   *
   * @param registration the raw registration string
   * @return a self-validated {@link AcademicRegistration} instance
   */
  public static AcademicRegistration factory(String registration) {
    String trimmed = StringUtils.trim(registration);
    AcademicRegistration vo = AcademicRegistration.builder().value(trimmed).build();
    vo.collectValidationProblems();
    return vo;
  }

  private void collectValidationProblems() {
    if (StringUtils.isEmpty(value)) {
      addFieldError(AcademicFieldErrorCodes.INVALID_REGISTRATION_BLANK);
      return;
    }
    if (value.length() > 15) {
      addFieldError(AcademicFieldErrorCodes.INVALID_REGISTRATION_TOO_LONG);
    }
  }
}
