package com.pug.helpers.domainGenerators;

import com.github.f4b6a3.uuid.UuidCreator;
import com.pug.geo.domain.City;
import com.pug.geo.domain.vos.IbgeCode;
import com.pug.helpers.domainGenerators.vos.IbgeCodeGenerator;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;

/** Helper to create City domain objects for tests. */
public final class CityGenerator {
  private final ThreadLocalRandom rnd = ThreadLocalRandom.current();
  private final IbgeCodeGenerator ibgeGen = new IbgeCodeGenerator();

  /** Pre-persist style: id=null, valid name, valid IBGE code. */
  public City randomCity() {
    return City.builder()
        .id(null)
        .name(randomCityName())
        .ibgeCode(ibgeGen.randomIbgeCode())
        .build();
  }

  /** Same as randomCity, but with a generated id. */
  public City randomCityWithId() {
    return City.builder()
        .id(UuidCreator.getTimeOrderedEpoch())
        .name(randomCityName())
        .ibgeCode(ibgeGen.randomIbgeCode())
        .build();
  }

  /** Build with a provided name and random valid IBGE code. */
  public City withName(String name) {
    return City.builder().id(null).name(name).ibgeCode(ibgeGen.randomIbgeCode()).build();
  }

  /** Build with a provided IBGE code and random valid name. */
  public City withIbge(IbgeCode code) {
    return City.builder().id(null).name(randomCityName()).ibgeCode(code).build();
  }

  /**
   * Generates a random city name with 1 to 3 words, each 3 to 12 letters long.
   *
   * @return A random city name not exceeding 100 characters.
   */
  private String randomCityName() {
    int words = rnd.nextInt(1, 4);
    StringBuilder b = new StringBuilder();
    for (int i = 0; i < words; i++) {
      if (i > 0) b.append(' ');
      b.append(capitalize(randomAlpha(3, 12)));
    }
    return b.length() <= 100 ? b.toString() : b.substring(0, 100);
  }

  private String randomAlpha(int min, int max) {
    int len = rnd.nextInt(min, max + 1);
    StringBuilder sb = new StringBuilder(len);
    for (int i = 0; i < len; i++) sb.append((char) rnd.nextInt('a', 'z' + 1));
    return sb.toString();
  }

  private static String capitalize(String s) {
    return Character.toUpperCase(s.charAt(0)) + s.substring(1).toLowerCase(Locale.ROOT);
  }
}
