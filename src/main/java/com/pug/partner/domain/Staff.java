package com.pug.partner.domain;

import static com.pug.partner.domain.PartnerErrorCodes.STAFF_EMAIL_INVALID;
import static com.pug.partner.domain.PartnerErrorCodes.STAFF_EMAIL_REQUIRED;
import static com.pug.partner.domain.PartnerErrorCodes.STAFF_EMAIL_TOO_LONG;
import static com.pug.partner.domain.PartnerErrorCodes.STAFF_ENTITY_REQUIRED;
import static com.pug.partner.domain.PartnerErrorCodes.STAFF_USER_REQUIRED;

import com.pug.shared.domain.exceptions.AppValidationException;
import com.pug.shared.domain.validation.EmailBasic;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Locale;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode(of = "id")
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(toBuilder = true)
public final class Staff {

  @NotNull private final UUID id;
  @NotNull private final UUID userId;

  @EmailBasic
  @Size(max = 254)
  private final String email;

  @NotNull private final UUID entityId;

  private final boolean active;

  /** Lowercased and trimmed email for uniqueness checks. */
  public String canonicalEmail() {
    return email == null ? null : email.trim().toLowerCase(Locale.ROOT);
  }

  public Staff activate() {
    return active ? this : toBuilder().active(true).build();
  }

  public Staff deactivate() {
    return active ? toBuilder().active(false).build() : this;
  }

  private void validate() {
    if (userId == null) throw new AppValidationException(STAFF_USER_REQUIRED);
    if (entityId == null) throw new AppValidationException(STAFF_ENTITY_REQUIRED);
    if (email == null || email.isBlank()) throw new AppValidationException(STAFF_EMAIL_REQUIRED);
    String e = email.trim();
    if (e.length() > 254) throw new AppValidationException(STAFF_EMAIL_TOO_LONG);
    int at = e.indexOf('@');
    int dot = e.indexOf('.', at + 1);
    if (at <= 0 || at == e.length() - 1 || dot <= at + 1 || dot == e.length() - 1) {
      throw new AppValidationException(STAFF_EMAIL_INVALID);
    }
  }

  public static class StaffBuilder {
    public StaffBuilder email(String email) {
      this.email = email == null ? null : email.trim();
      return this;
    }

    public Staff build() {
      String em = this.email == null ? null : this.email.trim();
      Staff s = new Staff(id, userId, em, entityId, active);
      s.validate();
      return s;
    }
  }

  public static StaffBuilder newActive() {
    return Staff.builder().active(true);
  }
}
