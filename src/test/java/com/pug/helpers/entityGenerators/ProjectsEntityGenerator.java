package com.pug.helpers.entityGenerators;

import com.pug.projects.infra.persistence.ProjectsEntity;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class ProjectsEntityGenerator {

  /**
   * Helper method to create a random ProjectsEntity object.
   *
   * @param entityId The UUID of the entity to which the project belongs.
   * @param userId The UUID of the user who created the project.
   */
  public ProjectsEntity createRandomProjectsEntity(UUID entityId, UUID userId) {
    BigDecimal offeredHours =
        BigDecimal.valueOf(ThreadLocalRandom.current().nextDouble(0.00, 1000.00));
    String status = generateRandomString(5, 16);
    OffsetDateTime createdAt = OffsetDateTime.now();
    OffsetDateTime closedAt =
        (ThreadLocalRandom.current().nextBoolean())
            ? null
            : createdAt.plusDays(ThreadLocalRandom.current().nextInt(1, 365));
    Integer maxParticipants =
        ThreadLocalRandom.current().nextBoolean()
            ? ThreadLocalRandom.current().nextInt(1, 100)
            : null;

    return ProjectsEntity.builder()
        .name(generateRandomString(5, 150))
        .entityId(entityId)
        .description(generateRandomString(10, 4000))
        .createdBy(userId)
        .createdAt(createdAt)
        .closedAt(closedAt)
        .offeredHours(offeredHours)
        .status(status)
        .maxParticipants(maxParticipants)
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
