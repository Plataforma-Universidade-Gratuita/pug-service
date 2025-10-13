package com.pug.partner.domain;

import com.pug.shared.domain.exceptions.AppValidationException;
import org.jetbrains.annotations.NotNull;

public record Address(String line) {
  public static Address of(String raw) {
    if (raw == null) return null;
    String t = raw.trim();
    if (t.isEmpty()) return null;
    if (t.length() > 254)
      throw new AppValidationException(PartnerErrorCodes.PARTNER_ADDRESS_TOO_LONG);
    return new Address(t);
  }

  @Override
  public @NotNull String toString() {
    return line;
  }
}
