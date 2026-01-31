package com.pug.identity.service.utils;

import com.pug.identity.domain.User;
import com.pug.identity.domain.vos.Cpf;
import com.pug.shared.time.TimeProvider;
import com.pug.shared.utils.StringUtils;

/** Utility class for processing User DTO inputs. */
public class UserProcessor {

  /**
   * Helper method to process DTO input and build a new User domain object.
   *
   * @param cpfString The CPF string from DTO.
   * @param name The user's name.
   * @param timeProvider The time provider for creation timestamp.
   * @return The constructed User domain object.
   */
  public static User processCreateInput(String cpfString, String name, TimeProvider timeProvider) {
    Cpf cpfVo = Cpf.factory(cpfString);
    return User.factory(cpfVo, name, timeProvider);
  }

  /**
   * Helper method to process DTO input and update an existing User domain object.
   *
   * @param existingUser The existing user to be updated.
   * @param cpfString The CPF string from DTO (can be null for no change).
   * @param name The name from DTO (can be null for no change).
   * @return The updated User domain object.
   */
  public static User processUpdateInput(User existingUser, String cpfString, String name) {

    User updatedUser = existingUser;

    if (!StringUtils.isEmpty(name)) {
      updatedUser = updatedUser.changeName(name);
    }

    if (!StringUtils.isEmpty(cpfString)) {
      Cpf newCpf = Cpf.factory(cpfString);
      updatedUser = updatedUser.changeCpf(newCpf);
    }

    return updatedUser;
  }
}
