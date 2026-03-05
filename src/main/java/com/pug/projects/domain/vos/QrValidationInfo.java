package com.pug.projects.domain.vos;

import com.pug.projects.domain.enums.ProjectsFieldErrorCodes;
import com.pug.shared.domain.DomainError;
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

  /** The geographic latitude where the attendance was recorded. */
  BigDecimal latitude;

  /** The geographic longitude where the attendance was recorded. */
  BigDecimal longitude;

  /** The unique cryptographic hash of the QR code used for validation. */
  String qrValidationHash;

  /**
   * Constructs a {@code QrValidationInfo} instance.
   *
   * @param duration the time duration recorded
   * @param latitude the geographic latitude
   * @param longitude the geographic longitude
   * @param qrValidationHash the unique QR hash
   */
  @Builder(toBuilder = true)
  private QrValidationInfo(
      BigDecimal duration, BigDecimal latitude, BigDecimal longitude, String qrValidationHash) {
    this.duration = duration;
    this.latitude = latitude;
    this.longitude = longitude;
    this.qrValidationHash = qrValidationHash;
  }

  /**
   * Factory method to create a new {@code QrValidationInfo} instance.
   *
   * <p>The instance is created and immediately self-validated.
   *
   * @param duration the time duration recorded
   * @param latitude the geographic latitude
   * @param longitude the geographic longitude
   * @param qrValidationHash the hash of the QR code used
   * @return a self-validated {@link QrValidationInfo} instance
   */
  public static QrValidationInfo factory(
      BigDecimal duration, BigDecimal latitude, BigDecimal longitude, String qrValidationHash) {
    QrValidationInfo vo =
        QrValidationInfo.builder()
            .duration(duration)
            .latitude(latitude)
            .longitude(longitude)
            .qrValidationHash(qrValidationHash)
            .build();
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
   *   <li>Both latitude and longitude must be provided together or completely omitted (appends
   *       {@link ProjectsFieldErrorCodes#INVALID_ATTENDANCE_GEO_INVALID_MISSING} if mismatched).
   *   <li>The latitude, if provided, must fall strictly between -90 and 90 degrees (appends {@link
   *       ProjectsFieldErrorCodes#INVALID_ATTENDANCE_GEO_INVALID_LAT}).
   *   <li>The longitude, if provided, must fall strictly between -180 and 180 degrees (appends
   *       {@link ProjectsFieldErrorCodes#INVALID_ATTENDANCE_GEO_INVALID_LONG}).
   * </ul>
   */
  private void collectValidationProblems() {
    if (duration == null || duration.signum() <= 0) {
      addFieldError(ProjectsFieldErrorCodes.INVALID_ATTENDANCE_DURATION_INVALID);
    }
    boolean hasLat = latitude != null;
    boolean hasLon = longitude != null;
    if (hasLat != hasLon) {
      addFieldError(ProjectsFieldErrorCodes.INVALID_ATTENDANCE_GEO_INVALID_MISSING);
    }
    if (hasLat) {
      if (latitude.compareTo(BigDecimal.valueOf(90)) > 0
          || latitude.compareTo(BigDecimal.valueOf(-90)) < 0) {
        addFieldError(ProjectsFieldErrorCodes.INVALID_ATTENDANCE_GEO_INVALID_LAT);
      }
    }
    if (hasLon) {
      if (longitude.compareTo(BigDecimal.valueOf(180)) > 0
          || longitude.compareTo(BigDecimal.valueOf(-180)) < 0) {
        addFieldError(ProjectsFieldErrorCodes.INVALID_ATTENDANCE_GEO_INVALID_LONG);
      }
    }
  }
}
