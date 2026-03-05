package com.pug.geo.service.utils;

import com.pug.geo.domain.City;
import com.pug.geo.domain.vos.IbgeCode;
import com.pug.shared.utils.StringUtils;

/**
 * Stateless utility class responsible for mapping raw DTO command data into pure Domain Aggregates
 * and Value Objects.
 *
 * <p>This processor centralizes the orchestration of domain factory methods and state-mutation
 * behaviors, ensuring that complex initialization or update logic does not pollute the application
 * service layer.
 */
public class CityProcessor {

  /**
   * Processes raw creation inputs and constructs a new {@link City} domain aggregate.
   *
   * <p>This method translates the raw string representations into appropriate Value Objects (e.g.,
   * {@link IbgeCode}) before passing them to the entity's factory method.
   *
   * <p><b>Note:</b> The returned {@link City} object may contain accumulated domain validation
   * failures. The caller is responsible for checking {@link City#hasFieldErrors()} and handling
   * them appropriately (e.g., throwing an {@code AppValidationException}).
   *
   * @param name the raw city name requested for creation
   * @param ibgeCodeString the raw 7-digit IBGE code string
   * @return a fully instantiated {@link City} domain aggregate, potentially containing validation
   *     errors
   */
  public static City processCreateInput(String name, String ibgeCodeString) {
    IbgeCode ibgeCodeVo = IbgeCode.factory(ibgeCodeString);
    return City.factory(name, ibgeCodeVo);
  }

  /**
   * Processes raw update inputs and conditionally mutates the state of an existing {@link City}.
   *
   * <p>This method applies partial updates. Only fields that are explicitly provided (i.e., not
   * null and not empty) will trigger a state mutation via the aggregate's domain behaviors.
   *
   * <p>Because domain entities in this system are modeled as immutable records, this method returns
   * a <i>new</i> instance of the {@link City} reflecting the applied changes. The caller must
   * verify {@link City#hasFieldErrors()} on the returned instance.
   *
   * @param existingCity the current, reconstituted {@link City} aggregate from the repository
   * @param name the proposed new city name, or {@code null}/empty to skip updating
   * @param ibgeCodeString the proposed new IBGE code string, or {@code null}/empty to skip updating
   * @return a new {@link City} domain aggregate reflecting the requested updates, potentially
   *     containing validation errors
   */
  public static City processUpdateInput(City existingCity, String name, String ibgeCodeString) {

    City updatedCity = existingCity;

    if (StringUtils.isNotEmpty(name)) {
      updatedCity = updatedCity.rename(name);
    }

    if (StringUtils.isNotEmpty(ibgeCodeString)) {
      IbgeCode newIbgeCode = IbgeCode.factory(ibgeCodeString);
      updatedCity = updatedCity.changeIbgeCode(newIbgeCode);
    }

    return updatedCity;
  }
}
