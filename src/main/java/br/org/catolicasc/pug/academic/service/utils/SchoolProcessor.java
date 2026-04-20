package br.org.catolicasc.pug.academic.service.utils;

import br.org.catolicasc.pug.academic.domain.School;
import br.org.catolicasc.pug.shared.utils.StringUtils;

/**
 * Stateless utility class responsible for mapping raw DTO command data into pure {@link School}
 * Domain Aggregates.
 */
public class SchoolProcessor {

  /**
   * Processes raw creation inputs and constructs a new {@link School} domain aggregate.
   *
   * <p><b>Note:</b> The caller is responsible for checking {@link School#hasFieldErrors()} on the
   * returned object and throwing standard validation exceptions if necessary.
   *
   * @param name the raw name of the school requested for creation
   * @return a fully instantiated {@link School} domain aggregate, potentially containing validation
   *     errors
   */
  public static School processCreateInput(String name) {
    return School.factory(name);
  }

  /**
   * Processes raw update inputs and conditionally mutates the state of an existing {@link School}.
   *
   * <p>Applies partial updates, returning a new immutable instance of the aggregate.
   *
   * @param existingSchool the current, reconstituted {@link School} aggregate from the repository
   * @param name the proposed new name, or {@code null}/empty to skip updating
   * @return a new {@link School} domain aggregate reflecting the requested updates, potentially
   *     containing validation errors
   */
  public static School processUpdateInput(School existingSchool, String name) {
    School updatedSchool = existingSchool;

    if (StringUtils.isNotEmpty(name)) {
      updatedSchool = updatedSchool.rename(name);
    }

    return updatedSchool;
  }
}
