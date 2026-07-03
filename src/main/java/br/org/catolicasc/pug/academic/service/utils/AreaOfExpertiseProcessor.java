/*
 * Copyright (c) 2026 Mateus Fernandes and Plataforma Universidade Gratuita.
 * All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized use,
 * copying, modification, distribution, or deployment is prohibited.
 */

package br.org.catolicasc.pug.academic.service.utils;

import br.org.catolicasc.pug.academic.domain.AreaOfExpertise;
import br.org.catolicasc.pug.shared.utils.StringUtils;

/**
 * Stateless utility class responsible for mapping raw DTO command data into pure {@link
 * AreaOfExpertise} Domain Aggregates.
 */
public final class AreaOfExpertiseProcessor {

  private AreaOfExpertiseProcessor() {}

  /**
   * Processes raw creation inputs and constructs a new {@link AreaOfExpertise} domain aggregate.
   *
   * <p><b>Note:</b> The caller is responsible for checking {@link AreaOfExpertise#hasFieldErrors()}
   * on the returned object and throwing standard validation exceptions if necessary.
   *
   * @param name the raw name of the areaOfExpertise requested for creation
   * @return a fully instantiated {@link AreaOfExpertise} domain aggregate, potentially containing
   *     validation errors
   */
  public static AreaOfExpertise processCreateInput(String name) {
    return AreaOfExpertise.factory(name);
  }

  /**
   * Processes raw update inputs and conditionally mutates the state of an existing {@link
   * AreaOfExpertise}.
   *
   * <p>Applies partial updates, returning a new immutable instance of the aggregate.
   *
   * @param existingAreaOfExpertise the current, reconstituted {@link AreaOfExpertise} aggregate
   *     from the repository
   * @param name the proposed new name, or {@code null}/empty to skip updating
   * @return a new {@link AreaOfExpertise} domain aggregate reflecting the requested updates,
   *     potentially containing validation errors
   */
  public static AreaOfExpertise processUpdateInput(
      AreaOfExpertise existingAreaOfExpertise, String name) {
    AreaOfExpertise updatedAreaOfExpertise = existingAreaOfExpertise;

    if (StringUtils.isNotEmpty(name)) {
      updatedAreaOfExpertise = updatedAreaOfExpertise.rename(name);
    }

    return updatedAreaOfExpertise;
  }
}
