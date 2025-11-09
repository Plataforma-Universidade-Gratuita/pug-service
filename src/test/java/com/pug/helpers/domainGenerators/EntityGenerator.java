package com.pug.helpers.domainGenerators;

import com.github.f4b6a3.uuid.UuidCreator;
import com.pug.helpers.CNPJGenerator;
import com.pug.partner.domain.Entity;
import com.pug.partner.domain.vos.Cnpj;
import java.util.concurrent.ThreadLocalRandom;

public final class EntityGenerator {
  private final ThreadLocalRandom rnd = ThreadLocalRandom.current();
  private final CNPJGenerator cnpjGen = new CNPJGenerator();
  private final CityGenerator cityGen = new CityGenerator();

  /** Pre-persist domain entity (id = null). */
  public Entity createRandomEntity() {
    return baseBuilder(false).build();
  }

  /** Persisted-like domain entity (id set). */
  public Entity createRandomPersistedEntity() {
    return baseBuilder(true).build();
  }

  private Entity.EntityBuilder baseBuilder(boolean withId) {
    return Entity.builder()
        .id(withId ? UuidCreator.getTimeOrderedEpoch() : null)
        .cnpj(new Cnpj(cnpjGen.generateRandomCNPJ()))
        .name(randomName(8, 30))
        .city(cityGen.randomCityWithId())
        .address(randomAscii(10, 60));
  }

  private String randomAscii(int min, int max) {
    int len = rnd.nextInt(min, max + 1);
    String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789 ";
    StringBuilder sb = new StringBuilder(len);
    for (int i = 0; i < len; i++) sb.append(chars.charAt(rnd.nextInt(chars.length())));
    return sb.toString().trim();
  }

  private String randomName(int min, int max) {
    int len = rnd.nextInt(min, max + 1);
    String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz ";
    StringBuilder sb = new StringBuilder(len);
    for (int i = 0; i < len; i++) sb.append(chars.charAt(rnd.nextInt(chars.length())));
    return sb.toString().trim();
  }
}
