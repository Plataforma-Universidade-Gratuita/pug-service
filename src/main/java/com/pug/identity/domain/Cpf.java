package com.pug.identity.domain;

import com.pug.shared.domain.exceptions.AppValidationException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@EqualsAndHashCode
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class Cpf {
  @Getter private final String value;

  public static Cpf of(String raw) {
    String d = digits(raw);
    if (!isValidDigits(d))
      throw new AppValidationException(IdentityErrorCodes.IDENTITY_CPF_INVALID);
    return new Cpf(d);
  }

  public String masked() {
    return "%s.%s.%s-%s"
        .formatted(
            value.substring(0, 3),
            value.substring(3, 6),
            value.substring(6, 9),
            value.substring(9, 11));
  }

  public static String digits(String raw) {
    return raw == null ? null : raw.replaceAll("\\D", "");
  }

  private static boolean isValidDigits(String d) {
    if (d == null || d.length() != 11) return false;
    if (d.chars().distinct().count() == 1) return false;
    int v1 = checkDigit(d, 10);
    int v2 = checkDigit(d, 11);
    return v1 == (d.charAt(9) - '0') && v2 == (d.charAt(10) - '0');
  }

  private static int checkDigit(String d, int weightStart) {
    int sum = 0;
    for (int i = 0; i < weightStart - 1; i++) sum += (d.charAt(i) - '0') * (weightStart - i);
    int mod = sum % 11;
    return (mod < 2) ? 0 : 11 - mod;
  }

  @Override
  public String toString() {
    return masked();
  }
}
