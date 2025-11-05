package com.pug.helpers.entityGenerators;

import com.pug.academic.infra.persistence.StudentsEntity;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class StudentsEntityGenerator {

  /**
   * Helper method to create a random StudentsEntity object.
   *
   * @param userId The UUID of the user.
   * @param courseId The UUID of the course.
   */
  public StudentsEntity createRandomStudentsEntity(UUID userId, UUID courseId) {
    BigDecimal requiredHours =
        BigDecimal.valueOf(ThreadLocalRandom.current().nextDouble(0.00, 1000.00));
    LocalDate startDate = LocalDate.now().minusDays(ThreadLocalRandom.current().nextInt(1, 365));
    LocalDate dueDate = startDate.plusDays(ThreadLocalRandom.current().nextInt(1, 365));

    return StudentsEntity.builder()
        .userId(userId)
        .academicRegistration(generateRandomString(15))
        .campus(generateRandomString(5, 150))
        .courseId(courseId)
        .requiredHours(requiredHours)
        .startDate(startDate)
        .dueDate(dueDate)
        .build();
  }

  /**
   * Helper method to generate a random string with a specified length. Characters will be uppercase
   * letters and digits only.
   */
  private String generateRandomString(int length) {
    StringBuilder sb = new StringBuilder(length);
    for (int i = 0; i < length; i++) {
      sb.append((char) ThreadLocalRandom.current().nextInt('A', 'Z' + 1));
    }
    return sb.toString();
  }

  /**
   * Helper method to generate a random string with a specified min and max length. Characters will
   * be uppercase letters and digits only.
   */
  private String generateRandomString(int minLength, int maxLength) {
    int length = ThreadLocalRandom.current().nextInt(minLength, maxLength + 1);
    return generateRandomString(length);
  }
}
