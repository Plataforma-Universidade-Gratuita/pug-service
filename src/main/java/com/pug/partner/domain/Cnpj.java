package com.pug.partner.domain;

import com.pug.shared.domain.exceptions.AppValidationException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@EqualsAndHashCode
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class Cnpj {
  @Getter private final String value;

  public static Cnpj of(String raw) {
    String d = digits(raw);
    if (d == null) throw new AppValidationException(PartnerErrorCodes.PARTNER_CNPJ_REQUIRED);
    if (d.length() != 14) throw new AppValidationException(PartnerErrorCodes.PARTNER_CNPJ_LENGTH);
    if (allSame(d)) throw new AppValidationException(PartnerErrorCodes.PARTNER_CNPJ_INVALID);
    if (!isValid(d)) throw new AppValidationException(PartnerErrorCodes.PARTNER_CNPJ_INVALID);
    return new Cnpj(d);
  }

  public static String digits(String raw) {
    if (raw == null) return null;
    return raw.replaceAll("\\D", "");
  }

  private static boolean allSame(String s) {
    char c = s.charAt(0);
    for (int i = 1; i < s.length(); i++) if (s.charAt(i) != c) return false;
    return true;
  }

  private static boolean isValid(String d) {
    return checkDigit(d, 12) == (d.charAt(12) - '0') && checkDigit(d, 13) == (d.charAt(13) - '0');
  }

  private static int checkDigit(String d, int pos) {
    int[] w1 = {5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
    int[] w2 = {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
    int[] w = pos == 12 ? w1 : w2;
    int sum = 0;
    for (int i = 0; i < w.length; i++) sum += (d.charAt(i) - '0') * w[i];
    int mod = sum % 11;
    return mod < 2 ? 0 : 11 - mod;
  }

  public String masked() {
    String v = value;
    return "%s.%s.%s/%s-%s"
        .formatted(
            v.substring(0, 2),
            v.substring(2, 5),
            v.substring(5, 8),
            v.substring(8, 12),
            v.substring(12));
  }

  @Override
  public String toString() {
    return masked();
  }
}
