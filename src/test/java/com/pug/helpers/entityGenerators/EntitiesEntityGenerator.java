package com.pug.helpers.entityGenerators;

import com.pug.helpers.CNPJGenerator;
import com.pug.partner.infra.persistence.EntitiesEntity;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class EntitiesEntityGenerator {

  CNPJGenerator cnpjGenerator;

  /**
   * Helper method to create a random EntitiesEntity object.
   *
   * @param cityId The UUID of the city to associate with the entity.
   */
  public EntitiesEntity createRandomEntitiesEntity(UUID cityId) {
    return EntitiesEntity.builder()
        .name(generateRandomString(5, 150))
        .cityId(cityId)
        .cnpj(cnpjGenerator.generateRandomCNPJ())
        .active(true)
        .build();
  }

  private String generateRandomString(int minLength, int maxLength) {
    int length = ThreadLocalRandom.current().nextInt(minLength, maxLength + 1);
    StringBuilder sb = new StringBuilder(length);
    for (int i = 0; i < length; i++) {
      sb.append((char) ThreadLocalRandom.current().nextInt('A', 'Z' + 1));
    }
    return sb.toString();
  }
}
