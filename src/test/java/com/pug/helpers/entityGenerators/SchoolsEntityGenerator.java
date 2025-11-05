package com.pug.helpers.entityGenerators;

import com.pug.academic.infra.persistence.SchoolsEntity;
import java.util.concurrent.ThreadLocalRandom;

public class SchoolsEntityGenerator {

  /** Helper method to create a random SchoolsEntity object. */
  public SchoolsEntity createRandomSchoolsEntity() {
    return SchoolsEntity.builder().name(generateRandomString(5, 100)).build();
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
