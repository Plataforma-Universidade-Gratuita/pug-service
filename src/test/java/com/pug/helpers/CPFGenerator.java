package com.pug.helpers;

import java.util.concurrent.ThreadLocalRandom;

public class CPFGenerator {

  /**
   * Helper method to generate a valid CPF (Brazilian personal identifier). CPF format is
   * XXX.XXX.XXX-XX, but it will return only the digits.
   */
  public String generateRandomCPF() {
    StringBuilder cpf = new StringBuilder();
    for (int i = 0; i < 9; i++) {
      cpf.append(ThreadLocalRandom.current().nextInt(0, 10));
    }

    int firstVerifier = calculateVerifierDigit(cpf.toString(), 10);
    cpf.append(firstVerifier);

    int secondVerifier = calculateVerifierDigit(cpf.toString(), 11);
    cpf.append(secondVerifier);

    return cpf.toString();
  }

  /**
   * Helper method to calculate a verification digit based on the CPF base digits.
   *
   * @param cpfBase The first 9 or 10 digits of the CPF.
   * @param multiplier The multiplier for the calculation (10 for first verifier, 11 for second).
   * @return The calculated verification digit.
   */
  private int calculateVerifierDigit(String cpfBase, int multiplier) {
    int sum = 0;
    for (int i = 0; i < cpfBase.length(); i++) {
      sum += (cpfBase.charAt(i) - '0') * (multiplier - i);
    }
    int remainder = sum % 11;
    return remainder < 2 ? 0 : 11 - remainder;
  }
}
