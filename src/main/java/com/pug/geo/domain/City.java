package com.pug.geo.domain;

import com.github.f4b6a3.uuid.UuidCreator;
import com.pug.geo.domain.enums.GeoErrorCodes;
import com.pug.geo.domain.vos.IbgeCode;
import com.pug.shared.domain.DomainError;
import com.pug.shared.exceptions.AppValidationException;
import com.pug.shared.utils.StringUtils;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.UUID;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Value;

/** City entity aggregate. */
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
   * Factory for new cities.
   *
   * @param name the name of the city
   * @param ibgeCode the IBGE code of the city
   * @return the created City instance
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
   * Behavior: change the city name.
   *
   * @param newName the new name of the city
   * @return the updated City instance
   */
  public City changeName(String newName) {
    var trimmedName = StringUtils.trim(newName);
    if (this.name.equals(trimmedName)) {
      return this;
    }

    City c = this.toBuilder().name(StringUtils.trim(newName)).build();
    c.collectValidationProblems();
    return c;
  }

  /**
   * Behavior: change IBGE code of the city.
   *
   * @param newCode the new IBGE code of the city
   * @return the updated City instance
   */
  public City changeIbgeCode(IbgeCode newCode) {
    if (this.ibgeCode.equals(newCode)) {
      return this;
    }

    City c = this.toBuilder().ibgeCode(newCode).build();
    c.collectValidationProblems();
    return c;
  }

  /** Validates the City instance and collects all validation problems. */
  private void collectValidationProblems() {
    if (id == null) {
      addError(new AppValidationException.Problem(GeoErrorCodes.INVALID_CITY_ID_BLANK));
    }

    if (StringUtils.isEmpty(name)) {
      addError(new AppValidationException.Problem(GeoErrorCodes.INVALID_CITY_NAME_BLANK));
    } else if (name.length() > 100) {
      addError(new AppValidationException.Problem(GeoErrorCodes.INVALID_CITY_NAME_LENGTH));
    }

    if (ibgeCode == null) {
      addError(new AppValidationException.Problem(GeoErrorCodes.INVALID_IBGE_CODE_BLANK));
    } else if (ibgeCode.hasErrors()) {
      addErrors(ibgeCode.getProblems());
    }
  }
}
