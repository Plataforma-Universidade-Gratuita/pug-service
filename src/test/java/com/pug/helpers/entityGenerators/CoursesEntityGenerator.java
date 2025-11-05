package com.pug.helpers.entityGenerators;

import com.pug.academic.infra.persistence.CoursesEntity;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class CoursesEntityGenerator {

  /**
   * Helper method to create a random CoursesEntity object.
   *
   * @param schoolId The UUID of the school to associate with the course.
   */
  public CoursesEntity createRandomCoursesEntity(UUID schoolId) {
    return CoursesEntity.builder().name(generateRandomString(5, 120)).schoolId(schoolId).build();
  }

  /**
   * Helper method to generate a random string with a specified length. Characters will be uppercase
   * letters only.
   */
  private String generateRandomString(int minLength, int maxLength) {
    int length = ThreadLocalRandom.current().nextInt(minLength, maxLength + 1);
    StringBuilder sb = new StringBuilder(length);
    for (int i = 0; i < length; i++) {
      sb.append(
          (char) ThreadLocalRandom.current().nextInt('A', 'Z' + 1)); // Random uppercase letters
    }
    return sb.toString();
  }
}
