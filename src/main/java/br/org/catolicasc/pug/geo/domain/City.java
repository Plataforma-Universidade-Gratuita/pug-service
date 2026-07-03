/*
 * Copyright (c) 2026 Mateus Fernandes and Plataforma Universidade Gratuita.
 * All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized use,
 * copying, modification, distribution, or deployment is prohibited.
 */

package br.org.catolicasc.pug.geo.domain;

import br.org.catolicasc.pug.geo.domain.enums.GeoFieldErrorCodes;
import br.org.catolicasc.pug.geo.domain.vos.IbgeCode;
import br.org.catolicasc.pug.shared.domain.DomainError;
import br.org.catolicasc.pug.shared.utils.StringUtils;
import com.github.f4b6a3.uuid.UuidCreator;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.UUID;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Value;

/**
 * Immutable Domain Entity representing a geographic City.
 *
 * <p>This class acts as an aggregate containing the city's unique identifier, name, and its
 * corresponding {@link IbgeCode}. It extends {@link DomainError} to accumulate validation failures
 * across its own fields and to bubble up errors from its nested value objects.
 */
@Getter
@Value
@EqualsAndHashCode(callSuper = false)
@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public class City extends DomainError {

  UUID id;

  String name;

  IbgeCode ibgeCode;

  @Builder(toBuilder = true)
  private City(UUID id, String name, IbgeCode ibgeCode) {
    this.id = id;
    this.name = name;
    this.ibgeCode = ibgeCode;
  }

  /**
   * Factory method to create a new {@code City} instance.
   *
   * <p>Automatically generates a time-ordered epoch UUID (UUIDv7) for the identifier, trims the
   * provided name, and performs a full validation of the entity and its contents.
   *
   * @param name the name of the city
   * @param ibgeCode the {@link IbgeCode} value object representing the city's IBGE code
   * @return a newly created and self-validated {@link City} instance
   */
  public static City factory(String name, IbgeCode ibgeCode) {
    City c =
        City.builder()
            .id(UuidCreator.getTimeOrderedEpoch())
            .name(StringUtils.trim(name))
            .ibgeCode(ibgeCode)
            .build();
    c.collectValidationProblems();
    return c;
  }

  /**
   * Updates the city's name.
   *
   * <p>Since this entity is immutable, this method returns a new {@code City} instance with the
   * updated, trimmed name if it differs from the current one. The new instance is fully
   * re-validated.
   *
   * @param newName the new name to assign to the city
   * @return a new, updated, and validated {@link City} instance, or {@code this} if the name is
   *     unchanged
   */
  public City rename(String newName) {
    var trimmedName = StringUtils.trim(newName);
    if (name.equals(trimmedName)) {
      return this;
    }
    City c = toBuilder().name(trimmedName).build();
    c.collectValidationProblems();
    return c;
  }

  /**
   * Updates the city's IBGE code.
   *
   * <p>Returns a new, re-validated {@code City} instance with the updated code, maintaining the
   * immutability of the domain entity.
   *
   * @param newCode the new {@link IbgeCode} to assign to the city
   * @return a new, updated, and validated {@link City} instance, or {@code this} if the code is
   *     unchanged
   */
  public City changeIbgeCode(IbgeCode newCode) {
    if (ibgeCode.equals(newCode)) {
      return this;
    }
    City c = toBuilder().ibgeCode(newCode).build();
    c.collectValidationProblems();
    return c;
  }

  private void collectValidationProblems() {
    validateIdField(id);
    validateNameField(name);
    if (ibgeCode == null) {
      addFieldError(GeoFieldErrorCodes.INVALID_IBGE_CODE_BLANK);
      return;
    }
    if (ibgeCode.hasFieldErrors()) {
      addFieldErrors(ibgeCode.getFieldErrors());
    }
  }
}
