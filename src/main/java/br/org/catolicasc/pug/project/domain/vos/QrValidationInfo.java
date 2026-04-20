package br.org.catolicasc.pug.project.domain.vos;

import br.org.catolicasc.pug.project.domain.enums.ProjectsFieldErrorCodes;
import br.org.catolicasc.pug.shared.domain.DomainError;
import br.org.catolicasc.pug.shared.utils.StringUtils;
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
public class QrValidationInfo extends DomainError {

  /** The duration of time spent on this attendance. */
  BigDecimal duration;

  /** The unique cryptographic hash of the QR code used for validation. */
  String qrValidationHash;

  /**
   * Constructs a {@code QrValidationInfo} instance.
   *
   * @param duration the time duration recorded
   * @param qrValidationHash the unique QR hash
   */
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

  /**
   * Evaluates internal constraints and accumulates validation problems.
   *
   * <p>Business rules applied:
   *
   * <ul>
   *   <li>The duration must not be null and must be strictly greater than zero (appends {@link
   *       ProjectsFieldErrorCodes#INVALID_ATTENDANCE_DURATION_INVALID}).
   *   <li>The qrValidationHash must not be null or an empty string (appends {@link
   *       ProjectsFieldErrorCodes#INVALID_ATTENDANCE_QR_VALIDATION_HASH_EMPTY}).
   * </ul>
   */
  private void collectValidationProblems() {
    if (duration == null || duration.signum() <= 0) {
      addFieldError(ProjectsFieldErrorCodes.INVALID_ATTENDANCE_DURATION_INVALID);
    }
    if (StringUtils.isEmpty(qrValidationHash)) {
      addFieldError(ProjectsFieldErrorCodes.INVALID_ATTENDANCE_QR_VALIDATION_HASH_EMPTY);
    }
  }
}
