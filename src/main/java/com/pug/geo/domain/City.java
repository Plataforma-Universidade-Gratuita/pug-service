package com.pug.geo.domain;

import com.github.f4b6a3.uuid.UuidCreator;
import com.pug.geo.domain.enums.GeoErrorCodes;
import com.pug.geo.domain.vos.IbgeCode;
import com.pug.shared.exceptions.AppValidationException;
import com.pug.shared.utils.StringUtils;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * City entity aggregate.
 */
@Getter
public class City {

  private final UUID id;
  private final String name;
  private final IbgeCode ibgeCode;

  @Builder(toBuilder = true)
  private City(UUID id, String name, IbgeCode ibgeCode) {
    this.id = id;
    this.name = name;
    this.ibgeCode = ibgeCode;
  }

  /**
   * Factory for new cities.
   *
   * @param name     the name of the city
   * @param ibgeCode the IBGE code of the city
   * @return the created City instance
   */
  public static City createNew(String name, IbgeCode ibgeCode) {
    City c = City.builder()
            .id(UuidCreator.getTimeOrderedEpoch())
            .name(StringUtils.trim(name))
            .ibgeCode(ibgeCode)
            .build();

    List<AppValidationException.Problem> problems = c.collectValidationProblems();
    if (!problems.isEmpty()) {
      throw new AppValidationException(problems);
    }
    return c;
  }

  /**
   * Behavior: change the city name.
   *
   * @param newName the new name of the city
   * @return the updated City instance
   */
  public City changeName(String newName) {
    City c = this.toBuilder().name(StringUtils.trim(newName)).build();
    List<AppValidationException.Problem> problems = c.collectValidationProblems();
    if (!problems.isEmpty()) {
      throw new AppValidationException(problems);
    }
    return c;
  }

  /**
   * Behavior: change IBGE code of the city.
   *
   * @param newCode the new IBGE code of the city
   * @return the updated City instance
   */
  public City changeIbgeCode(IbgeCode newCode) {
    City c = this.toBuilder().ibgeCode(newCode).build();
    List<AppValidationException.Problem> problems = c.collectValidationProblems();
    if (!problems.isEmpty()) {
      throw new AppValidationException(problems);
    }
    return c;
  }

  /**
   * Validates the City instance and collects all validation problems.
   *
   * @return A list of {@code AppValidationException.Problem} if any validation fails; an empty list otherwise.
   */
  private List<AppValidationException.Problem> collectValidationProblems() {
    List<AppValidationException.Problem> problems = new ArrayList<>();

    if (id == null) {
      problems.add(new AppValidationException.Problem(GeoErrorCodes.INVALID_CITY_ID_BLANK, "id"));
    }
    if (StringUtils.isEmpty(name)) {
      problems.add(new AppValidationException.Problem(GeoErrorCodes.INVALID_CITY_NAME_BLANK, "name"));
    }
    if (name.length() > 100) {
      problems.add(new AppValidationException.Problem(GeoErrorCodes.INVALID_CITY_NAME_LENGTH, "name"));
    }
    if (ibgeCode == null) {
      problems.add(new AppValidationException.Problem(GeoErrorCodes.INVALID_IBGE_CODE_BLANK, "ibgeCode"));
    }

    return problems;
  }
}