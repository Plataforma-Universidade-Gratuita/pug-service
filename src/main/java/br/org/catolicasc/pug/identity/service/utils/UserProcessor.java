/*
 * Copyright (c) 2026 Mateus Fernandes and Plataforma Universidade Gratuita.
 * All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized use,
 * copying, modification, distribution, or deployment is prohibited.
 */

package br.org.catolicasc.pug.identity.service.utils;

import br.org.catolicasc.pug.identity.domain.User;
import br.org.catolicasc.pug.identity.domain.vos.Cpf;
import br.org.catolicasc.pug.identity.service.dtos.users.UserCreateCommand;
import br.org.catolicasc.pug.shared.exceptions.AppValidationException;
import br.org.catolicasc.pug.shared.utils.CollectionUtils;
import br.org.catolicasc.pug.shared.utils.StringUtils;
import java.util.ArrayList;
import java.util.List;

/**
 * Stateless utility class responsible for mapping raw DTO command data into pure {@link User}
 * Domain Aggregates and Value Objects.
 *
 * <p>This processor centralizes the orchestration of domain factory methods and state-mutation
 * behaviors, ensuring that complex initialization or update logic does not pollute the application
 * service layer.
 */
public final class UserProcessor {

  private UserProcessor() {}

  /**
   * Processes a bulk list of user creation commands, generating a list of pure Domain Aggregates.
   *
   * <p>This method maps each command to its underlying domain entity and triggers internal
   * validations. If any user violates domain rules, an exception is thrown to abort the entire
   * batch transaction.
   *
   * @param cmds the {@link List} of bulk user creation commands
   * @return a {@link List} of instantiated and validated {@link User} aggregates
   * @throws AppValidationException if any aggregate contains domain validation errors
   */
  public static List<User> processBulkCreateInput(List<UserCreateCommand> cmds) {
    if (CollectionUtils.isEmpty(cmds)) {
      return List.of();
    }

    List<User> users = new ArrayList<>(cmds.size());
    for (UserCreateCommand cmd : cmds) {
      User user = processCreateInput(cmd.cpfString(), cmd.name());
      if (user.hasFieldErrors()) {
        throw new AppValidationException(user.getFieldErrors());
      }
      users.add(user);
    }
    return users;
  }

  /**
   * Processes raw creation inputs and constructs a new {@link User} domain aggregate.
   *
   * <p>This method translates the raw string representations into appropriate Value Objects (e.g.,
   * {@link Cpf}) before passing them to the entity's factory method.
   *
   * <p><b>Note:</b> The returned {@link User} object may contain accumulated domain validation
   * failures. The caller is responsible for checking {@link User#hasFieldErrors()} and handling
   * them appropriately.
   *
   * @param cpfString the raw 11-digit CPF string requested for creation
   * @param name the raw name of the user
   * @return a fully instantiated {@link User} domain aggregate, potentially containing validation
   *     errors
   */
  public static User processCreateInput(String cpfString, String name) {
    Cpf cpfVo = Cpf.factory(cpfString);
    return User.factory(cpfVo, name);
  }

  /**
   * Processes raw update inputs and conditionally mutates the state of an existing {@link User}.
   *
   * <p>This method applies partial updates. Only fields that are explicitly provided (i.e., not
   * null and not empty) will trigger a state mutation via the aggregate's domain behaviors.
   *
   * <p>Because domain entities in this system are modeled as immutable records, this method returns
   * a <i>new</i> instance of the {@link User} reflecting the applied changes.
   *
   * @param existingUser the current, reconstituted {@link User} aggregate from the repository
   * @param name the proposed new name, or {@code null}/empty to skip updating
   * @return a new {@link User} domain aggregate reflecting the requested updates, potentially
   *     containing validation errors
   */
  public static User processUpdateInput(User existingUser, String name) {
    User updatedUser = existingUser;

    if (StringUtils.isNotEmpty(name)) {
      updatedUser = updatedUser.rename(name);
    }

    return updatedUser;
  }
}
