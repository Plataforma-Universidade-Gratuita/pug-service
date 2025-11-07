package com.pug.helpers.domainGenerators;

import com.github.f4b6a3.uuid.UuidCreator;
import com.pug.helpers.CPFGenerator;
import com.pug.identity.domain.User;
import com.pug.identity.domain.vos.Cpf;
import com.pug.identity.domain.vos.Email;
import com.pug.shared.domain.enums.AccountType;
import java.time.OffsetDateTime;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;

public final class UserGenerator {
  private final ThreadLocalRandom rnd = ThreadLocalRandom.current();
  private final CPFGenerator cpfGen = new CPFGenerator();

  /** Pre-persist user: id=null, createdAt=null, active=true, no password hash. */
  public User createRandomUser() {
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

  /** Persisted-like user: id set, createdAt set. Useful when a non-null id is required. */
  public User createRandomPersistedUser() {
    OffsetDateTime created = OffsetDateTime.now().minusSeconds(rnd.nextLong(60, 86_400));
    return User.builder()
        .id(UuidCreator.getTimeOrderedEpoch())
        .cpf(new Cpf(cpfGen.generateRandomCPF()))
        .name(randomName())
        .email(new Email(randomEmail()))
        .accountType(randomAccountType())
        .passwordHash(null)
        .active(Boolean.TRUE)
        .createdAt(created)
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
    return vals[rnd.nextInt(vals.length)];
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

  /** Backward-compat alias. */
  @Deprecated
  public User randomUser() {
    return createRandomUser();
  }
}
