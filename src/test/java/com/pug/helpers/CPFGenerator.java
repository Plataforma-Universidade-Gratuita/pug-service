package com.pug.helpers;

import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class CPFGenerator {

  private static final Set<String> BLACKLIST =
      Set.of(
          "00000000000",
          "11111111111",
          "22222222222",
          "33333333333",
          "44444444444",
          "55555555555",
          "66666666666",
          "77777777777",
          "88888888888",
          "99999999999",
          "12345678909");

  /** Returns 11 digits, no punctuation. */
  public String generateRandomCPF() {
    ThreadLocalRandom rnd = ThreadLocalRandom.current();
    while (true) {
      String base9 = random9(rnd);
      if (allSame(base9)) continue;

      int d1 = calcDigit(base9, 10);
      int d2 = calcDigit(base9 + d1, 11);
      String cpf = base9 + d1 + d2;

      if (!BLACKLIST.contains(cpf)) return cpf;
    }
  }

  /**
   * Generates a random string of 9 digits.
   *
   * @param rnd the random number generator.
   * @return a string of 9 random digits.
   */
  private static String random9(ThreadLocalRandom rnd) {
    StringBuilder s = new StringBuilder(9);
    for (int i = 0; i < 9; i++) s.append(rnd.nextInt(10));
    return s.toString();
  }

  /**
   * Checks if all characters in the string are the same.
   *
   * @param s the string to check.
   * @return true if all characters are the same, false otherwise.
   */
  private static boolean allSame(String s) {
    char c = s.charAt(0);
    for (int i = 1; i < s.length(); i++) if (s.charAt(i) != c) return false;
    return true;
  }

  /**
   * Calculates one CPF digit.
   *
   * @param digits the digits to calculate from.
   * @param startWeight the starting weight (10 for first digit, 11 for second).
   * @return the calculated digit.
   */
  private static int calcDigit(String digits, int startWeight) {
    int sum = 0;
    for (int i = 0; i < digits.length(); i++) {
      sum += (digits.charAt(i) - '0') * (startWeight - i);
    }
    int rem = sum % 11;
    return rem < 2 ? 0 : 11 - rem;
  }
}
