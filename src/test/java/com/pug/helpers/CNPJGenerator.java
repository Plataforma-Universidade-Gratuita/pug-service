package com.pug.helpers;

import java.util.concurrent.ThreadLocalRandom;

public class CNPJGenerator {

  /**
   * Helper method to generate a valid CNPJ (Brazilian company identifier). CNPJ format is
   * XX.XXX.XXX/0001-XX, but it will return only the digits.
   */
  public String generateRandomCNPJ() {
    StringBuilder cnpj = new StringBuilder();
    for (int i = 0; i < 12; i++) {
      cnpj.append(ThreadLocalRandom.current().nextInt(0, 10));
    }

    int firstVerifier = calculateVerifierDigit(cnpj.toString(), 5);
    cnpj.append(firstVerifier);

    int secondVerifier = calculateVerifierDigit(cnpj.toString(), 6);
    cnpj.append(secondVerifier);

    return cnpj.toString();
  }

  /**
   * Helper method to calculate a verification digit based on the CNPJ base digits.
   *
   * @param cnpjBase The first 12 digits of the CNPJ.
   * @param multiplier The multiplier for the calculation (5 for the first verifier, 6 for the
   *     second).
   * @return The calculated verification digit.
   */
  private int calculateVerifierDigit(String cnpjBase, int multiplier) {
    int sum = 0;
    int[] weights;

    if (multiplier == 5) {
      weights = new int[] {5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
    } else {
      weights = new int[] {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3};
    }

    for (int i = 0; i < cnpjBase.length(); i++) {
      sum += (cnpjBase.charAt(i) - '0') * weights[i];
    }

    int remainder = sum % 11;
    return remainder < 2 ? 0 : 11 - remainder;
  }
}
