package com.pug.partner.domain;

import com.pug.geo.domain.City;
import com.pug.partner.domain.enums.PartnerErrorCodes;
import com.pug.partner.domain.vos.Cnpj;
import com.pug.shared.exceptions.AppValidationException;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/** Domain model representing an Entity with validation logic. */
@Getter
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Entity {
  private final UUID id;
  private final Cnpj cnpj;
  private final String name;
  private final City city;
  private final String address;

  /**
   * Validates the Entity instance fields according to business rules.
   *
   * @throws AppValidationException if any validation rule is violated.
   */
  private void validate() {
    if (cnpj == null) {
      throw new AppValidationException(PartnerErrorCodes.INVALID_CNPJ);
    }
    if (name == null || name.isBlank()) {
      throw new AppValidationException(PartnerErrorCodes.INVALID_NAME_BLANK);
    }
    if (name.length() > 150) {
      throw new AppValidationException(PartnerErrorCodes.INVALID_NAME_TOOLONG);
    }
    if (city == null) {
      throw new AppValidationException(PartnerErrorCodes.INVALID_CITY);
    }
    if (address != null && address.length() > 254) {
      throw new AppValidationException(PartnerErrorCodes.INVALID_ADDRESS_TOOLONG);
    }
  }

  /** Builder class for constructing Entity instances with validation. */
  public static class EntityBuilder {
    /**
     * Builds the Entity instance and performs validation.
     *
     * @return a validated Entity instance.
     * @throws AppValidationException if validation fails.
     */
    public Entity build() {
      Entity e = new Entity(id, cnpj, name, city, address);
      e.validate();
      return e;
    }
  }
}
