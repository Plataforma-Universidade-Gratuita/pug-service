package com.pug.helpers.entityGenerators;

import com.pug.projects.infra.persistence.EnrollmentsEntity;
import java.time.OffsetDateTime;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class EnrollmentsEntityGenerator {

  /**
   * Helper method to create a random EnrollmentsEntity object.
   *
   * @param projectId The UUID of the project.
   * @param userId The UUID of the student.
   */
  public EnrollmentsEntity createRandomEnrollmentsEntity(UUID projectId, UUID userId) {
    String status = generateRandomString(5, 16);
    OffsetDateTime requestAt = OffsetDateTime.now();
    OffsetDateTime acceptedAt =
        (ThreadLocalRandom.current().nextBoolean())
            ? null
            : requestAt.plusDays(ThreadLocalRandom.current().nextInt(1, 30));
    OffsetDateTime closingStatusAt =
        (ThreadLocalRandom.current().nextBoolean())
            ? null
            : acceptedAt != null
                ? acceptedAt.plusDays(ThreadLocalRandom.current().nextInt(1, 30))
                : requestAt.plusDays(ThreadLocalRandom.current().nextInt(1, 30));

    return EnrollmentsEntity.builder()
        .id(new EnrollmentsEntity.EnrollmentsId(projectId, userId))
        .status(status)
        .requestAt(requestAt)
        .acceptedAt(acceptedAt)
        .closingStatusAt(closingStatusAt)
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
