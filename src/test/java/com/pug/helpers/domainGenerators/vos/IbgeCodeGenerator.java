package com.pug.helpers.domainGenerators.vos;

import com.pug.geo.domain.vos.IbgeCode;
import java.util.concurrent.ThreadLocalRandom;

/** Helper to create IbgeCode values for tests. */
public final class IbgeCodeGenerator {
  private final ThreadLocalRandom rnd = ThreadLocalRandom.current();

  /** Returns a valid 7-digit IbgeCode. */
  public IbgeCode randomIbgeCode() {
    return new IbgeCode(randomDigits(7));
  }

  /** Returns a random 7-digit string (useful for JSON payloads). */
  public String randomIbgeCodeString() {
    return randomDigits(7);
  }

  /** Returns a clearly invalid code string for negative tests. */
  public String randomInvalidCodeString() {
    if (rnd.nextBoolean()) {
      int len = rnd.nextBoolean() ? 6 : 8;
      return randomDigits(len);
    }
    String s = randomDigits(7);
    int pos = rnd.nextInt(7);
    char ch = (char) rnd.nextInt('A', 'Z' + 1);
    return s.substring(0, pos) + ch + s.substring(pos + 1);
  }

  private String randomDigits(int len) {
    StringBuilder sb = new StringBuilder(len);
    for (int i = 0; i < len; i++) sb.append((char) ('0' + rnd.nextInt(10)));
    return sb.toString();
  }
}
