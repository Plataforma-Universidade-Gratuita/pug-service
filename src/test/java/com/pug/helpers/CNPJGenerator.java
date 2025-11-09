package com.pug.helpers;

import java.util.concurrent.ThreadLocalRandom;

public class CNPJGenerator {

  public String generateRandomCNPJ() {
    StringBuilder cnpj = new StringBuilder(14);
    for (int i = 0; i < 12; i++) {
      cnpj.append(ThreadLocalRandom.current().nextInt(0, 10));
    }

    int d1 = calculateVerifierDigit(cnpj.toString(), 5);
    cnpj.append(d1);

    int d2 = calculateVerifierDigit(cnpj.toString(), 6);
    cnpj.append(d2);

    return cnpj.toString();
  }

  /**
   * @param phase 5 for first digit, 6 for second digit
   */
  private int calculateVerifierDigit(String cnpjBase, int phase) {
    final int[] weights =
        (phase == 5)
            ? new int[] {5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2}
            : new int[] {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};

    int sum = 0;
    for (int i = 0; i < weights.length; i++) {
      sum += (cnpjBase.charAt(i) - '0') * weights[i];
    }
    int r = sum % 11;
    return (r < 2) ? 0 : 11 - r;
  }
}
