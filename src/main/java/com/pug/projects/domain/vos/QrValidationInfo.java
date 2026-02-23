package com.pug.projects.domain.vos;

import com.pug.projects.domain.enums.ProjectsErrorCodes;
import com.pug.shared.domain.DomainError;
import com.pug.shared.domain.Problem;
import java.math.BigDecimal;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Value;

/** Value object representing QR code validation information. */
@Getter
@Value
@EqualsAndHashCode(callSuper = false)
public class QrValidationInfo extends DomainError {

  BigDecimal duration;
  BigDecimal latitude;
  BigDecimal longitude;
  String qrValidationHash;

  /** Private constructor to enforce the use of the factory method. */
  @Builder(toBuilder = true)
  private QrValidationInfo(
      BigDecimal duration, BigDecimal latitude, BigDecimal longitude, String qrValidationHash) {
    this.duration = duration;
    this.latitude = latitude;
    this.longitude = longitude;
    this.qrValidationHash = qrValidationHash;
  }

  /**
   * Factory method to create and validate a QrValidationInfo instance.
   *
   * @param duration The duration of the spent time on this attendance.
   * @param latitude The latitude where the attendance was recorded.
   * @param longitude The longitude where the attendance was recorded.
   * @param qrValidationHash The hash of the QR code used for validation.
   * @return A validated QrValidationInfo instance.
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

  /** Validates the QrValidationInfo fields and accumulates errors if any. */
  private void collectValidationProblems() {
    if (duration == null || duration.signum() <= 0) {
      addError(new Problem(ProjectsErrorCodes.INVALID_ATTENDANCE_DURATION_INVALID));
    }

    boolean hasLat = latitude != null;
    boolean hasLon = longitude != null;

    if (hasLat != hasLon) {
      addError(new Problem(ProjectsErrorCodes.INVALID_ATTENDANCE_GEO_INVALID_MISSING));
    }

    if (hasLat) {
      if (latitude.compareTo(BigDecimal.valueOf(90)) > 0
          || latitude.compareTo(BigDecimal.valueOf(-90)) < 0) {
        addError(new Problem(ProjectsErrorCodes.INVALID_ATTENDANCE_GEO_INVALID_LAT));
      }
    }

    if (hasLon) {
      if (longitude.compareTo(BigDecimal.valueOf(180)) > 0
          || longitude.compareTo(BigDecimal.valueOf(-180)) < 0) {
        addError(new Problem(ProjectsErrorCodes.INVALID_ATTENDANCE_GEO_INVALID_LONG));
      }
    }
  }
}
