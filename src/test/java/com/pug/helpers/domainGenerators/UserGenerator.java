package com.pug.helpers.domainGenerators;

import com.pug.helpers.CPFGenerator;
import com.pug.identity.domain.User;
import com.pug.identity.domain.vos.Cpf;
import com.pug.identity.domain.vos.Email;
import com.pug.shared.domain.enums.AccountType;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;

/** Helper class to generate random User instances for testing purposes. */
public final class UserGenerator {
  private final ThreadLocalRandom rnd = ThreadLocalRandom.current();
  private final CPFGenerator cpfGen = new CPFGenerator();

  /** Pre-persist user: id=null, createdAt=null, active=true, no password hash. */
  public User randomUser() {
    return User.builder()
        .id(null)
        .cpf(new Cpf(cpfGen.generateRandomCPF()))
        .name(randomName())
        .email(new Email(randomEmail()))
        .accountType(randomAccountType())
        .passwordHash(null)
        .active(Boolean.TRUE)
        .createdAt(null)
        .build();
  }

  // ---- helpers ----
  private String randomEmail() {
    String local = randomAlphaNum(6, 12).toLowerCase(Locale.ROOT);
    return local + "@example.com";
  }

  private String randomName() {
    int words = rnd.nextInt(2, 4);
    StringBuilder b = new StringBuilder();
    for (int i = 0; i < words; i++) {
      if (i > 0) b.append(' ');
      b.append(capitalize(randomAlpha(3, 8)));
    }
    return b.toString();
  }

  private AccountType randomAccountType() {
    AccountType[] vals = AccountType.values();
    return vals[ThreadLocalRandom.current().nextInt(vals.length)];
  }

  private static String capitalize(String s) {
    return Character.toUpperCase(s.charAt(0)) + s.substring(1);
  }

  private String randomAlpha(int min, int max) {
    int len = rnd.nextInt(min, max + 1);
    StringBuilder sb = new StringBuilder(len);
    for (int i = 0; i < len; i++) sb.append((char) rnd.nextInt('a', 'z' + 1));
    return sb.toString();
  }

  private String randomAlphaNum(int min, int max) {
    String chars = "abcdefghijklmnopqrstuvwxyz0123456789";
    int len = rnd.nextInt(min, max + 1);
    StringBuilder sb = new StringBuilder(len);
    for (int i = 0; i < len; i++) sb.append(chars.charAt(rnd.nextInt(chars.length())));
    return sb.toString();
  }
}
