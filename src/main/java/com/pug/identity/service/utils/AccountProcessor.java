package com.pug.identity.service.utils;

import com.pug.identity.domain.Account;
import com.pug.identity.domain.vos.Email;
import com.pug.shared.domain.enums.AccountType;
import com.pug.shared.utils.StringUtils;
import java.util.UUID;

/** Utility class for processing Account DTO inputs. */
public class AccountProcessor {

  /**
   * Helper method to process DTO input and build a new Account domain object.
   *
   * @param userId The ID of the user owner.
   * @param emailString The email string from DTO.
   * @param accountTypeStr The account type string (e.g., "INDIVIDUAL", "COMPANY").
   * @param passwordHash The hashed password.
   * @return The constructed Account domain object.
   */
  public static Account processCreateInput(
      UUID userId, String emailString, String accountTypeStr, String passwordHash) {

    Email emailVo = Email.factory(emailString);
    AccountType type =
        StringUtils.isEmpty(accountTypeStr) ? null : AccountType.valueOf(accountTypeStr);

    return Account.factory(userId, emailVo, type, passwordHash);
  }

  /**
   * Helper method to process DTO input and update an existing Account domain object.
   *
   * @param existingAccount The existing account to be updated.
   * @param emailString The email string from DTO (can be null for no change).
   * @param passwordHash The new password hash (can be null for no change).
   * @return The updated Account domain object.
   */
  public static Account processUpdateInput(
      Account existingAccount, String emailString, String passwordHash) {

    Account updatedAccount = existingAccount;

    if (StringUtils.isNotEmpty(emailString)) {
      Email newEmail = Email.factory(emailString);
      updatedAccount = updatedAccount.changeEmail(newEmail);
    }

    if (StringUtils.isNotEmpty(passwordHash)) {
      updatedAccount = updatedAccount.changePasswordHash(passwordHash);
    }

    return updatedAccount;
  }
}
