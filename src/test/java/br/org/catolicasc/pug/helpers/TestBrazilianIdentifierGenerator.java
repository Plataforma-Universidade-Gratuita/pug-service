package br.org.catolicasc.pug.helpers;

import java.util.Random;

/**
 * Static utility class for generating valid, random Brazilian identification numbers (CPF and CNPJ)
 * in test scenarios.
 *
 * <p>Each call produces a unique, algorithmically valid identifier with correct check digits,
 * eliminating the need for hardcoded values and preventing collisions across parallel test runs.
 */
public final class TestBrazilianIdentifierGenerator {

  private static final Random RANDOM = new Random();

  private TestBrazilianIdentifierGenerator() {}

  /**
   * Generates a valid random CPF (Cadastro de Pessoas Físicas) string.
   *
   * <p>The first 9 digits are randomized and the two check digits are computed using the standard
   * Brazilian CPF algorithm.
   *
   * @return a valid 11-digit numeric CPF string
   */
  public static String generateValidCpf() {
    int[] cpf = new int[11];
    for (int i = 0; i < 9; i++) {
      cpf[i] = RANDOM.nextInt(10);
    }
    int sum = 0;
    for (int i = 0; i < 9; i++) {
      sum += cpf[i] * (10 - i);
    }
    cpf[9] = (sum % 11 < 2) ? 0 : 11 - (sum % 11);
    sum = 0;
    for (int i = 0; i < 10; i++) {
      sum += cpf[i] * (11 - i);
    }
    cpf[10] = (sum % 11 < 2) ? 0 : 11 - (sum % 11);

    StringBuilder sb = new StringBuilder();
    for (int digit : cpf) {
      sb.append(digit);
    }
    return sb.toString();
  }

  /**
   * Generates a valid random CNPJ (Cadastro Nacional da Pessoa Jurídica) string.
   *
   * <p>The first 12 digits are randomized and the two check digits are computed using the standard
   * Brazilian CNPJ algorithm.
   *
   * @return a valid 14-digit numeric CNPJ string
   */
  public static String generateValidCnpj() {
    int[] cnpj = new int[14];
    for (int i = 0; i < 12; i++) {
      cnpj[i] = RANDOM.nextInt(10);
    }
    int[] weight1 = {5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
    cnpj[12] = calculateCnpjDigit(cnpj, weight1);
    int[] weight2 = {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
    cnpj[13] = calculateCnpjDigit(cnpj, weight2);

    StringBuilder sb = new StringBuilder();
    for (int digit : cnpj) {
      sb.append(digit);
    }
    return sb.toString();
  }

  private static int calculateCnpjDigit(int[] cnpj, int[] weights) {
    int sum = 0;
    for (int i = 0; i < weights.length; i++) {
      sum += cnpj[i] * weights[i];
    }
    int remainder = sum % 11;
    return (remainder < 2) ? 0 : 11 - remainder;
  }
}
