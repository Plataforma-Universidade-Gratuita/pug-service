package com.pug.helpers.entityGenerators;

import com.pug.helpers.CPFGenerator;
import com.pug.identity.infra.persistence.UsersEntity;
import io.quarkus.test.junit.QuarkusTest;
import java.time.OffsetDateTime;
import java.util.concurrent.ThreadLocalRandom;

@QuarkusTest
public class UsersEntityGenerator {

  /** Helper method to create a random UsersEntity object. */
  public UsersEntity createRandomUsersEntity() {
    return UsersEntity.builder()
        .cpf(generateRandomCPF())
        .name(generateRandomString(5, 150))
        .email(generateRandomEmail())
        .accountType(generateRandomString(5, 16))
        .passwordHash(generateRandomString(8, 255))
        .active(true)
        .createdAt(OffsetDateTime.now())
        .build();
  }

  /**
   * Helper method to generate a random CPF (Brazilian personal identifier). CPF format is
   * XXXXXXXXXXX
   */
  public String generateRandomCPF() {
    CPFGenerator cpfGenerator = new CPFGenerator();
    return cpfGenerator.generateRandomCPF();
  }

  /** Helper method to generate a random email address. Example: randomName1234@example.com */
  private String generateRandomEmail() {
    return generateRandomString(5, 10) + "@example.com";
  }

  /**
   * Helper method to generate a random string of a specified length. Characters will be upper-case
   * letters only.
   */
  private String generateRandomString(int minLength, int maxLength) {
    int length = ThreadLocalRandom.current().nextInt(minLength, maxLength + 1);
    StringBuilder sb = new StringBuilder(length);
    for (int i = 0; i < length; i++) {
      sb.append((char) ThreadLocalRandom.current().nextInt('A', 'Z' + 1));
    }
    return sb.toString();
  }
}
