package com.pug.helpers.entityGenerators;

import com.pug.projects.infra.persistence.AttendancesEntity;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class AttendancesEntityGenerator {
  /**
   * Helper method to create a random AttendancesEntity object.
   *
   * @param projectId The project ID to set in the entity.
   * @param userId The student ID to set in the entity.
   */
  public AttendancesEntity createRandomAttendancesEntity(UUID projectId, UUID userId) {
    BigDecimal duration = BigDecimal.valueOf(ThreadLocalRandom.current().nextDouble(0.01, 24.00));
    BigDecimal latitude =
        BigDecimal.valueOf(ThreadLocalRandom.current().nextDouble(-90.000000, 90.000000));
    BigDecimal longitude =
        BigDecimal.valueOf(ThreadLocalRandom.current().nextDouble(-180.000000, 180.000000));
    String status = generateRandomString(5, 16);
    String qrValidationHash =
        ThreadLocalRandom.current().nextBoolean() ? generateRandomString(32, 32) : null;
    UUID validatedBy = ThreadLocalRandom.current().nextBoolean() ? UUID.randomUUID() : null;
    OffsetDateTime validatedAt =
        (ThreadLocalRandom.current().nextBoolean())
            ? null
            : OffsetDateTime.now().plusDays(ThreadLocalRandom.current().nextInt(1, 30));
    OffsetDateTime createdAt = OffsetDateTime.now();

    return AttendancesEntity.builder()
        .projectId(projectId)
        .studentId(userId)
        .duration(duration)
        .latitude(latitude)
        .longitude(longitude)
        .status(status)
        .qrValidationHash(qrValidationHash)
        .validatedBy(validatedBy)
        .validatedAt(validatedAt)
        .createdAt(createdAt)
        .build();
  }

  /**
   * Helper method to generate a random string with a specified length. Characters will be uppercase
   * letters only.
   */
  private String generateRandomString(int minLength, int maxLength) {
    int length = ThreadLocalRandom.current().nextInt(minLength, maxLength + 1);
    StringBuilder sb = new StringBuilder(length);
    for (int i = 0; i < length; i++) {
      sb.append((char) ThreadLocalRandom.current().nextInt('A', 'Z' + 1));
    }
    return sb.toString();
  }
}
