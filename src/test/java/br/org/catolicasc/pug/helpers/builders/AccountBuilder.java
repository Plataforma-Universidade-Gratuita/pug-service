package br.org.catolicasc.pug.helpers.builders;

import br.org.catolicasc.pug.identity.domain.Account;
import br.org.catolicasc.pug.identity.domain.vos.Email;
import br.org.catolicasc.pug.shared.domain.enums.AccountType;
import java.util.Random;
import java.util.UUID;

/**
 * Builder class for creating {@link Account} domain aggregates in test scenarios.
 *
 * <p>Provides a fluent API to define account properties, with sensible defaults for mandatory
 * fields like user linkage and authentication email.
 */
public class AccountBuilder {
  private UUID userId = UUID.randomUUID();
  private String email = generateUniqueEmail();
  private AccountType type = getRandomAccountType();

  private AccountBuilder() {}

  /**
   * Initializes a new instance of the AccountBuilder.
   *
   * @return a new AccountBuilder instance
   */
  public static AccountBuilder anAccount() {
    return new AccountBuilder();
  }

  /**
   * Helper to retrieve a random account type.
   *
   * @return a randomly selected {@link AccountType}
   */
  private AccountType getRandomAccountType() {
    AccountType[] values = AccountType.values();
    return values[new Random().nextInt(values.length)];
  }

  /**
   * Sets the user ID linked to this account.
   *
   * @param userId the UUID of the user
   * @return this builder instance
   */
  public AccountBuilder forUser(UUID userId) {
    this.userId = userId;
    return this;
  }

  /**
   * Sets the email address for this account.
   *
   * @param email the email string
   * @return this builder instance
   */
  public AccountBuilder withEmail(String email) {
    this.email = email;
    return this;
  }

  /**
   * Sets the account type.
   *
   * @param type the {@link AccountType} role
   * @return this builder instance
   */
  public AccountBuilder withType(AccountType type) {
    this.type = type;
    return this;
  }

  /**
   * Generates a unique email address using a random UUID to ensure compatibility with database
   * unique constraints across multiple test runs.
   *
   * @return a unique email string
   */
  private String generateUniqueEmail() {
    return "test-" + UUID.randomUUID().toString().substring(0, 8) + "@pug.com";
  }

  /**
   * Constructs the {@link Account} aggregate using the current builder state.
   *
   * @return a configured {@link Account} instance
   */
  public Account build() {
    String passwordHash = "hashed-password";
    return Account.factory(userId, Email.factory(email), type, passwordHash);
  }
}
