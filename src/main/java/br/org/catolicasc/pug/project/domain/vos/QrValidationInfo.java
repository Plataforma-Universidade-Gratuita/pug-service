/*
 * Copyright (c) 2026 Mateus Fernandes and Plataforma Universidade Gratuita.
 * All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized use,
 * copying, modification, distribution, or deployment is prohibited.
 */

package br.org.catolicasc.pug.project.domain.vos;

import br.org.catolicasc.pug.project.domain.enums.ProjectsFieldErrorCodes;
import br.org.catolicasc.pug.shared.domain.DomainError;
import br.org.catolicasc.pug.shared.utils.StringUtils;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.math.BigDecimal;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Value;

/**
 * Immutable Value Object (VO) representing geographic and temporal QR code validation data.
 *
 * <p>Extends {@link DomainError} to encapsulate and accumulate validations ensuring that geographic
 * coordinates strictly adhere to valid global boundaries, and that recorded time durations are
 * logically positive.
 */
@Getter
@Value
@EqualsAndHashCode(callSuper = false)
@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public class QrValidationInfo extends DomainError {

  BigDecimal duration;

  String qrValidationHash;

  @Builder(toBuilder = true)
  private QrValidationInfo(BigDecimal duration, String qrValidationHash) {
    this.duration = duration;
    this.qrValidationHash = qrValidationHash;
  }

  /**
   * Factory method to create a new {@code QrValidationInfo} instance.
   *
   * <p>The instance is created and immediately self-validated.
   *
   * @param duration the time duration recorded
   * @param qrValidationHash the hash of the QR code used
   * @return a self-validated {@link QrValidationInfo} instance
   */
  public static QrValidationInfo factory(BigDecimal duration, String qrValidationHash) {
    QrValidationInfo vo =
        QrValidationInfo.builder().duration(duration).qrValidationHash(qrValidationHash).build();
    vo.collectValidationProblems();
    return vo;
  }

  private void collectValidationProblems() {
    if (duration == null || duration.signum() <= 0) {
      addFieldError(ProjectsFieldErrorCodes.INVALID_ATTENDANCE_DURATION_INVALID);
    }
    if (StringUtils.isEmpty(qrValidationHash)) {
      addFieldError(ProjectsFieldErrorCodes.INVALID_ATTENDANCE_QR_VALIDATION_HASH_EMPTY);
    }
  }
}
