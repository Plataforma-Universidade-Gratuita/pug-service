package com.pug.identity.service.utils;

import com.pug.identity.domain.User;
import com.pug.identity.domain.vos.Cpf;
import com.pug.shared.utils.StringUtils;

/**
 * Stateless utility class responsible for mapping raw DTO command data into pure {@link User}
 * Domain Aggregates and Value Objects.
 *
 * <p>This processor centralizes the orchestration of domain factory methods and state-mutation
 * behaviors, ensuring that complex initialization or update logic does not pollute the application
 * service layer.
 */
public class UserProcessor {

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
