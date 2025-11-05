package com.pug.helpers.entityGenerators;

import com.pug.geo.infra.persistence.CitiesEntity;
import java.util.concurrent.ThreadLocalRandom;

public class CitiesEntityGenerator {

  /** Helper method to create a random CitiesEntity object. */
  public CitiesEntity createRandomCitiesEntity() {
    return CitiesEntity.builder()
        .name(generateRandomString(1, 100))
        .ibgeCode(generateRandomIBGECode())
        .build();
  }

  /**
   * Helper method to generate a random string with a specified length. Characters will be uppercase
   * letters.
   */
  private String generateRandomString(int minLength, int maxLength) {
    int length = ThreadLocalRandom.current().nextInt(minLength, maxLength + 1);
    StringBuilder sb = new StringBuilder(length);
    for (int i = 0; i < length; i++) {
      sb.append((char) ThreadLocalRandom.current().nextInt('A', 'Z' + 1));
    }
    return sb.toString();
  }

  /** Helper method to generate a valid 7-digit IBGE code. */
  private String generateRandomIBGECode() {
    StringBuilder ibgeCode = new StringBuilder();
    for (int i = 0; i < 7; i++) {
      ibgeCode.append(ThreadLocalRandom.current().nextInt(0, 10));
    }
    return ibgeCode.toString();
  }
}
