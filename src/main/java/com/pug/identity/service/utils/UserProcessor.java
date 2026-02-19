package com.pug.identity.service.utils;

import com.pug.identity.domain.User;
import com.pug.identity.domain.vos.Cpf;
import com.pug.shared.utils.StringUtils;

/**
 * Utility class for processing User DTO inputs.
 */
public class UserProcessor {

  /**
   * Helper method to process DTO input and build a new User domain object.
   *
   * @param cpfString The CPF string from DTO.
   * @param name      The user's name.
   * @return The constructed User domain object.
   */
  public static User processCreateInput(String cpfString, String name) {
    Cpf cpfVo = Cpf.factory(cpfString);
    return User.factory(cpfVo, name);
  }

  /**
   * Helper method to process DTO input and update an existing User domain object.
   *
   * @param existingUser The existing user to be updated.
   * @param cpfString    The CPF string from DTO (can be null for no change).
   * @param name         The name from DTO (can be null for no change).
   * @return The updated User domain object.
   */
  public static User processUpdateInput(User existingUser, String cpfString, String name) {

    User updatedUser = existingUser;

    if (StringUtils.isNotEmpty(name)) {
      updatedUser = updatedUser.changeName(name);
    }

    if (StringUtils.isNotEmpty(cpfString)) {
      Cpf newCpf = Cpf.factory(cpfString);
      updatedUser = updatedUser.changeCpf(newCpf);
    }

    return updatedUser;
  }
}
