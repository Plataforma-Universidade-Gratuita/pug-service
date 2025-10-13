package com.pug.partner.domain;

import static com.pug.partner.domain.PartnerErrorCodes.PARTNER_CITY_REQUIRED;
import static com.pug.partner.domain.PartnerErrorCodes.PARTNER_CNPJ_REQUIRED;
import static com.pug.partner.domain.PartnerErrorCodes.PARTNER_NAME_REQUIRED;
import static com.pug.partner.domain.PartnerErrorCodes.PARTNER_NAME_TOO_LONG;

import com.pug.shared.domain.exceptions.AppValidationException;
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
public final class PartnerEntity {
  private final UUID id;
  private final Cnpj cnpj;
  private final String name;
  private final UUID cityId;
  private final Address address;
  private final boolean active;

  private void validate() {
    if (cnpj == null) throw new AppValidationException(PARTNER_CNPJ_REQUIRED);
    if (name == null || name.isBlank()) throw new AppValidationException(PARTNER_NAME_REQUIRED);
    if (name.length() > 150) throw new AppValidationException(PARTNER_NAME_TOO_LONG);
    if (cityId == null) throw new AppValidationException(PARTNER_CITY_REQUIRED);
  }

  public PartnerEntity activate() {
    return active ? this : toBuilder().active(true).build();
  }

  public PartnerEntity deactivate() {
    return active ? toBuilder().active(false).build() : this;
  }

  public static class PartnerEntityBuilder {
    public PartnerEntityBuilder name(String name) {
      this.name = name == null ? null : name.trim();
      return this;
    }

    public PartnerEntity build() {
      String nm = this.name == null ? null : this.name.trim();
      PartnerEntity x = new PartnerEntity(id, cnpj, nm, cityId, address, active);
      x.validate();
      return x;
    }
  }

  public static PartnerEntityBuilder newActive() {
    return PartnerEntity.builder().active(true);
  }
}
